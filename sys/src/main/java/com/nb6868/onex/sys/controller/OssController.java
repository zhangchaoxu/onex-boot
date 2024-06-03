package com.nb6868.onex.sys.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONObject;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.oss.AbstractOssService;
import com.nb6868.onex.common.oss.OssFactory;
import com.nb6868.onex.common.oss.OssPropsConfig;
import com.nb6868.onex.common.params.BaseParamsService;
import com.nb6868.onex.common.pojo.IdsForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.MultipartFileUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.sys.dto.OssFileBase64UploadForm;
import com.nb6868.onex.sys.dto.OssPresignedUrlForm;
import com.nb6868.onex.sys.dto.OssQueryForm;
import com.nb6868.onex.sys.dto.OssStsForm;
import com.nb6868.onex.sys.entity.OssEntity;
import com.nb6868.onex.sys.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sys/oss/")
@Validated
@Tag(name = "存储管理")
@Slf4j
public class OssController {

    @Autowired
    OssService ossService;
    @Autowired
    BaseParamsService paramsService;

    @PostMapping("uploadAnon")
    @Operation(summary = "匿名上传文件(文件形式)")
    @AccessControl
    @ApiOperationSupport(order = 10)
    public Result<?> uploadAnon(@RequestParam(required = false, defaultValue = "OSS_PUBLIC") String paramsCode,
                                @RequestParam(required = false) String prefix,
                                @RequestPart MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(paramsCode, OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");
        // 有两个前缀，config定义前缀和用户上传前缀
        String pathPrefix = uploadService.buildPathPrefix(ossConfig.getPrefix(), prefix);
        String objectKey = uploadService.buildObjectKey(ossConfig.getBucketName(), pathPrefix, ossConfig.getPathPolicy(), file.getOriginalFilename(), true);
        String url = uploadService.upload(objectKey, file);

        Dict result = Dict.create().set("src", url).set("filename", file.getOriginalFilename());
        if (ossConfig.getSaveDb()) {
            //保存文件信息
            OssEntity oss = new OssEntity();
            oss.setUrl(url);
            oss.setFilename(file.getOriginalFilename());
            oss.setSize(file.getSize());
            oss.setContentType(file.getContentType());
            ossService.save(oss);
            result.set("oss", oss);
        }
        return new Result<>().success(result);
    }

    @PostMapping("upload")
    @Operation(summary = "上传文件(文件形式)")
    @ApiOperationSupport(order = 20)
    public Result<?> upload(@RequestParam(required = false, defaultValue = "OSS_PUBLIC") String paramsCode,
                            @RequestParam(required = false) String prefix,
                            @RequestPart MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(paramsCode, OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");
        // 有两个前缀，config定义前缀和用户上传前缀
        String pathPrefix = uploadService.buildPathPrefix(ossConfig.getPrefix(), prefix);
        String objectKey = uploadService.buildObjectKey(ossConfig.getBucketName(), pathPrefix, ossConfig.getPathPolicy(), file.getOriginalFilename(), true);
        String url = uploadService.upload(objectKey, file);
        Dict result = Dict.create().set("src", url).set("filename", file.getOriginalFilename());
        if (ossConfig.getSaveDb()) {
            //保存文件信息
            OssEntity oss = new OssEntity();
            oss.setUrl(url);
            oss.setFilename(file.getOriginalFilename());
            oss.setSize(file.getSize());
            oss.setContentType(file.getContentType());
            ossService.save(oss);
            result.set("oss", oss);
        }
        return new Result<>().success(result);
    }

    @PostMapping("uploadToTemp")
    @Operation(summary = "上传到临时文件(文件形式)")
    @ApiOperationSupport(order = 20)
    public Result<?> uploadTemp(@RequestPart MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        File localFile = MultipartFileUtils.multipartFileToFile(file);
        AssertUtils.isNull(localFile, ErrorCode.OSS_UPLOAD_FILE_ERROR);

        Dict result = Dict.create().set("filePath", localFile.getAbsolutePath()).set("filename", file.getOriginalFilename());
        return new Result<>().success(result);
    }

    @PostMapping("uploadExcelToTemp")
    @Operation(summary = "上传Excel到临时文件(文件形式)")
    @ApiOperationSupport(order = 20)
    public Result<?> uploadExcelToTemp(@RequestPart MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        File localFile = MultipartFileUtils.multipartFileToFile(file);
        AssertUtils.isFalse(localFile != null && localFile.exists(), ErrorCode.OSS_UPLOAD_FILE_ERROR);

        List<Object> titles = new ArrayList<>();
        try {
            ExcelReader reader = ExcelUtil.getReader(localFile);
            titles = reader.readRow(0);
        } catch (Exception e) {
            log.error("Excel文件读取失败", e);
        }
        Dict result = Dict.create().set("filePath", localFile.getAbsolutePath()).set("columns", titles).set("filename", file.getOriginalFilename());
        return new Result<>().success(result);
    }

    @PostMapping("anonUploadBase64")
    @Operation(summary = "匿名上传单文件(base64)", description = "@Anon")
    @AccessControl
    @ApiOperationSupport(order = 30)
    public Result<?> anonUploadBase64(@Validated @RequestBody OssFileBase64UploadForm form) {
        // 将base64转成file
        MultipartFile file = MultipartFileUtils.base64ToMultipartFile(form.getFileBase64().getFileBase64());
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(form.getParamsCode(), OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        // 有两个前缀，config定义前缀和用户上传前缀
        String pathPrefix = uploadService.buildPathPrefix(ossConfig.getPrefix(), form.getPrefix());
        String objectKey = uploadService.buildObjectKey(ossConfig.getBucketName(), pathPrefix, ossConfig.getPathPolicy(), form.getFileBase64().getFilaName(), true);
        String url = uploadService.upload(objectKey, file);
        Dict result = Dict.create().set("src", url);
        if (ossConfig.getSaveDb()) {
            //保存文件信息
            OssEntity oss = new OssEntity();
            oss.setUrl(url);
            oss.setFilename(file.getOriginalFilename());
            oss.setSize(file.getSize());
            oss.setContentType(file.getContentType());
            ossService.save(oss);
            result.set("oss", oss);
        }
        return new Result<>().success(result);
    }

    @PostMapping("uploadBase64")
    @Operation(summary = "上传单文件(base64)")
    @ApiOperationSupport(order = 40)
    public Result<?> uploadBase64(@Validated @RequestBody OssFileBase64UploadForm form) {
        // 将base64转成file
        MultipartFile file = MultipartFileUtils.base64ToMultipartFile(form.getFileBase64().getFileBase64());
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(form.getParamsCode(), OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        // 有两个前缀，config定义前缀和用户上传前缀
        String pathPrefix = uploadService.buildPathPrefix(ossConfig.getPrefix(), form.getPrefix());
        String objectKey = uploadService.buildObjectKey(ossConfig.getBucketName(), pathPrefix, ossConfig.getPathPolicy(), form.getFileBase64().getFilaName(), true);
        String url = uploadService.upload(objectKey, file);

        Dict result = Dict.create().set("src", url);
        if (ossConfig.getSaveDb()) {
            //保存文件信息
            OssEntity oss = new OssEntity();
            oss.setUrl(url);
            oss.setFilename(file.getOriginalFilename());
            oss.setSize(file.getSize());
            oss.setContentType(file.getContentType());
            ossService.save(oss);
            result.set("oss", oss);
        }
        return new Result<>().success(result);
    }

    @PostMapping("anonUploadMulti")
    @Operation(summary = "匿名上传多文件", description = "@Anon")
    @ApiOperationSupport(order = 50)
    public Result<?> anonUploadMulti(@RequestParam(required = false, defaultValue = "OSS_PUBLIC") String paramsCode,
                                     @RequestParam(required = false) String prefix,
                                     @RequestPart @NotEmpty(message = "文件不能为空") MultipartFile[] files) {
        List<String> srcList = new ArrayList<>();
        List<OssEntity> ossList = new ArrayList<>();
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(paramsCode, OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");
        // 有两个前缀，config定义前缀和用户上传前缀
        String pathPrefix = uploadService.buildPathPrefix(ossConfig.getPrefix(), prefix);
        for (MultipartFile file : files) {
            // 上传文件
            String objectKey = uploadService.buildObjectKey(ossConfig.getBucketName(), pathPrefix, ossConfig.getPathPolicy(), file.getOriginalFilename(), true);
            String url = uploadService.upload(objectKey, file);
            if (ossConfig.getSaveDb()) {
                //保存文件信息
                OssEntity oss = new OssEntity();
                oss.setUrl(url);
                oss.setFilename(file.getOriginalFilename());
                oss.setSize(file.getSize());
                oss.setContentType(file.getContentType());
                ossService.save(oss);
                srcList.add(url);
                ossList.add(oss);
            }
        }

        return new Result<>().success(Dict.create().set("src", CollUtil.join(srcList, ",")).set("oss", ossList));
    }

    @PostMapping("uploadMulti")
    @Operation(summary = "上传多文件")
    @ApiOperationSupport(order = 60)
    public Result<?> uploadMulti(@RequestParam(required = false, defaultValue = "OSS_PUBLIC") String paramsCode,
                                 @RequestParam(required = false) String prefix,
                                 @RequestPart @NotEmpty(message = "文件不能为空") MultipartFile[] files) {
        List<String> srcList = new ArrayList<>();
        List<OssEntity> ossList = new ArrayList<>();
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(paramsCode, OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");
        // 有两个前缀，config定义前缀和用户上传前缀
        String pathPrefix = uploadService.buildPathPrefix(ossConfig.getPrefix(), prefix);
        for (MultipartFile file : files) {
            // 上传文件
            String objectKey = uploadService.buildObjectKey(ossConfig.getBucketName(), pathPrefix, ossConfig.getPathPolicy(), file.getOriginalFilename(), true);
            String url = uploadService.upload(objectKey, file);
            if (ossConfig.getSaveDb()) {
                //保存文件信息
                OssEntity oss = new OssEntity();
                oss.setUrl(url);
                oss.setFilename(file.getOriginalFilename());
                oss.setSize(file.getSize());
                oss.setContentType(file.getContentType());
                ossService.save(oss);
                srcList.add(url);
                ossList.add(oss);
            }
        }

        return new Result<>().success(Dict.create().set("src", CollUtil.join(srcList, ",")).set("oss", ossList));
    }

    @PostMapping("getPresignedUrl")
    @Operation(summary = "获得授权访问地址")
    @ApiOperationSupport(order = 70)
    public Result<?> getPresignedUrl(@Validated @RequestBody OssPresignedUrlForm form) {
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(form.getParamsCode(), OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        String url = uploadService.getPresignedUrl(form.getObjectName(), form.getExpiration());

        return new Result<>().success(url);
    }

    @PostMapping("getSts")
    @Operation(summary = "获得STS临时访问token")
    @ApiOperationSupport(order = 80)
    public Result<?> getSts(@Validated @RequestBody OssStsForm form) {
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(form.getParamsCode(), OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        JSONObject result = uploadService.getSts();
        return new Result<>().success(result);
    }

    @PostMapping("page")
    @Operation(summary = "分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:oss", "sys:oss:query"}, logical = Logical.OR)
    @ApiOperationSupport(order = 100)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody OssQueryForm form) {
        PageData<?> page = ossService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("deleteBatch")
    @Operation(summary = "批量删除")
    @LogOperation("批量删除")
    @ApiOperationSupport(order = 110)
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:oss", "sys:oss:delete"}, logical = Logical.OR)
    public Result<?> deleteBatch(@Validated @RequestBody IdsForm form) {
        ossService.removeByIds(form.getIds());

        return new Result<>();
    }

}
