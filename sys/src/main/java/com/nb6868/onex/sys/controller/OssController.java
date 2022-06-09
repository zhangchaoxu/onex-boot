package com.nb6868.onex.sys.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.oss.AbstractOssService;
import com.nb6868.onex.common.oss.OssFactory;
import com.nb6868.onex.common.oss.OssPropsConfig;
import com.nb6868.onex.common.params.BaseParamsService;
import com.nb6868.onex.common.pojo.IdsTenantForm;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sys/oss/")
@Validated
@Api(tags = "存储管理", position = 40)
public class OssController {

    @Autowired
    private OssService ossService;
    @Autowired
    private BaseParamsService paramsService;

    @PostMapping("uploadAnon")
    @ApiOperation(value = "匿名上传文件(文件形式)", notes = "@Anon")
    @AccessControl
    public Result<?> uploadAnon(@RequestParam(required = false, defaultValue = "OSS_PUBLIC", name = "OSS配置参数") String paramsCode, @RequestParam("file") MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(paramsCode, OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        String url = uploadService.upload(file);
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

    @PostMapping("upload")
    @ApiOperation(value = "上传文件(文件形式)")
    public Result<?> upload(@RequestParam(required = false, defaultValue = "OSS_PUBLIC", name = "OSS配置参数") String paramsCode, @RequestParam("file") MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(paramsCode, OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        String url = uploadService.upload(file);
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

    @PostMapping("anonUploadBase64")
    @ApiOperation(value = "匿名上传单文件(base64)", notes = "@Anon")
    @AccessControl
    public Result<?> anonUploadBase64(@Validated @RequestBody OssFileBase64UploadForm form) {
        // 将base64转成file
        MultipartFile file = MultipartFileUtils.base64ToMultipartFile(form.getFileBase64().getFileBase64());
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(form.getParamsCode(), OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        String url = uploadService.upload(file);
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
    @ApiOperation(value = "上传单文件(base64)")
    public Result<?> uploadBase64(@Validated @RequestBody OssFileBase64UploadForm form) {
        // 将base64转成file
        MultipartFile file = MultipartFileUtils.base64ToMultipartFile(form.getFileBase64().getFileBase64());
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(form.getParamsCode(), OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        String url = uploadService.upload(file);
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
    @ApiOperation(value = "匿名上传多文件", notes = "@Anon")
    public Result<?> anonUploadMulti(@RequestParam(required = false, defaultValue = "OSS_PUBLIC", name = "OSS配置参数") String paramsCode,
                                 @RequestParam("file") @NotEmpty(message = "文件不能为空") MultipartFile[] files) {
        List<String> srcList = new ArrayList<>();
        List<OssEntity> ossList = new ArrayList<>();
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(paramsCode, OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");
        for (MultipartFile file : files) {
            // 上传文件
            String url = uploadService.upload(file);
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
    @ApiOperation(value = "上传多文件")
    public Result<?> uploadMulti(@RequestParam(required = false, defaultValue = "OSS_PUBLIC", name = "OSS配置参数") String paramsCode,
                                 @RequestParam("file") @NotEmpty(message = "文件不能为空") MultipartFile[] files) {
        List<String> srcList = new ArrayList<>();
        List<OssEntity> ossList = new ArrayList<>();
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(paramsCode, OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");
        for (MultipartFile file : files) {
            // 上传文件
            String url = uploadService.upload(file);
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
    @ApiOperation(value = "获得授权访问地址")
    public Result<?> getPresignedUrl(@Validated @RequestBody OssPresignedUrlForm form) {
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(form.getParamsCode(), OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        String url = uploadService.getPresignedUrl(form.getObjectName(), form.getExpiration());

        return new Result<>().success(url);
    }

    @PostMapping("getSts")
    @ApiOperation(value = "获得STS临时访问token")
    public Result<?> getSts(@Validated @RequestBody OssStsForm form) {
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(form.getParamsCode(), OssPropsConfig.class, null);
        AssertUtils.isNull(ossConfig, "未定义的参数配置");
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        JSONObject result = uploadService.getSts();
        return new Result<>().success(result);
    }

    @PostMapping("page")
    @ApiOperation(value = "分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sys:oss:page")
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody OssQueryForm form) {
        PageData<?> page = ossService.pageDto(form.getPage(), QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("sys:oss:delete")
    public Result<?> deleteBatch(@Validated @RequestBody IdsTenantForm form) {
        ossService.logicDeleteByWrapper(QueryWrapperHelper.getPredicate(form));

        return new Result<>();
    }

}
