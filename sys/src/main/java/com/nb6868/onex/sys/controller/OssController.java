package com.nb6868.onex.sys.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.config.NonStaticResourceHttpRequestConfig;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.oss.*;
import com.nb6868.onex.common.params.BaseParamsService;
import com.nb6868.onex.common.pojo.*;
import com.nb6868.onex.common.util.MultipartFileUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.sys.SysConst;
import com.nb6868.onex.sys.dto.*;
import com.nb6868.onex.sys.entity.OssEntity;
import com.nb6868.onex.sys.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController("SysOss")
@RequestMapping("/sys/oss/")
@Validated
@Tag(name = "存储管理")
@Slf4j
public class OssController {

    @Autowired
    OssService ossService;
    @Autowired
    BaseParamsService paramsService;
    @Autowired
    NonStaticResourceHttpRequestConfig nonStaticResourceHttpRequestConfig;

    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传文件(文件形式)")
    public Result<FileUuidItem> upload(@RequestParam(required = false, defaultValue = SysConst.OSS_PUBLIC) String paramsCode,
                                        @RequestParam(required = false) String prefix,
                                        @RequestPart MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(paramsCode, OssPropsConfig.class, null);
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");
        String objectKey = uploadService.buildObjectKey(prefix, file.getOriginalFilename());
        ApiResult<JSONObject> uploadResult = uploadService.upload(objectKey, file);
        AssertUtils.isFalse(uploadResult.isSuccess(), uploadResult.getCodeMsg());
        FileUuidItem result = new FileUuidItem().setUrl(ossConfig.getDomain() + objectKey).setName(file.getOriginalFilename());
        if (ossConfig.getSaveDb()) {
            //保存文件信息
            OssEntity oss = new OssEntity();
            oss.setUrl(ossConfig.getDomain() + objectKey);
            oss.setFilename(file.getOriginalFilename());
            oss.setSize(file.getSize());
            oss.setContentType(file.getContentType());
            oss.setType(prefix);
            oss.setPath(objectKey);
            oss.setUuid(IdUtil.fastUUID());
            ossService.save(oss);
            result.setUuid(oss.getUuid());
        }
        return new Result<FileUuidItem>().success(result);
    }

    @GetMapping("download/{uuid}")
    @AccessControl("download/**")
    @Operation(summary = "文件下载", description = "Anon")
    public ResponseEntity<?> download(@PathVariable("uuid") String uuid) throws IOException {
        OssEntity entity = ossService.getByUuid(uuid);
        AssertUtils.isNull(entity, "文件记录不存在");
        File file = ResourceUtils.getFile(entity.getPath());
        AssertUtils.isFalse(file.exists(), "文件不存在");
        AssertUtils.isFalse(file.canRead(), "文件读取失败");
        // 文件名编码，防止中文乱码
        String filename = URLEncoder.encode(entity.getFilename(), StandardCharsets.UTF_8);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, StrUtil.format(OssLocalUtils.FILENAME_FMT, filename))
                .header(HttpHeaders.CONTENT_TYPE, StrUtil.blankToDefault(entity.getContentType(), MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .body(new FileSystemResource(file));
    }

    @GetMapping("preview/{uuid}")
    @AccessControl("preview/**")
    @Operation(summary = "预览文件(对image和video做预览)", description = "Anon")
    public void preview(@PathVariable("uuid") String uuid, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        OssEntity entity = ossService.getByUuid(uuid);
        AssertUtils.isNull(entity, "文件记录不存在");
        File file = ResourceUtils.getFile(entity.getPath());
        AssertUtils.isFalse(file.exists(), "文件不存在");
        AssertUtils.isFalse(file.canRead(), "文件读取失败");
        // 文件名编码，防止中文乱码
        String filename = URLEncoder.encode(entity.getFilename(), StandardCharsets.UTF_8);
        httpServletResponse.addHeader(HttpHeaders.CONTENT_DISPOSITION, StrUtil.format(OssLocalUtils.FILENAME_FMT, filename));
        httpServletResponse.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
        httpServletResponse.setContentType(StrUtil.blankToDefault(entity.getContentType(), MediaType.APPLICATION_OCTET_STREAM_VALUE));
        httpServletRequest.setAttribute(NonStaticResourceHttpRequestConfig.ATTR_FILE, entity.getPath());
        nonStaticResourceHttpRequestConfig.handleRequest(httpServletRequest, httpServletResponse);
    }

    @PostMapping("uploadToTemp")
    @Operation(summary = "上传到临时文件", description = "不支持分布式环境")
    public Result<?> uploadTemp(@RequestPart MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        File localFile = MultipartFileUtils.multipartFileToFile(file);
        AssertUtils.isNull(localFile, ErrorCode.OSS_UPLOAD_FILE_ERROR);

        Dict result = Dict.create().set("filePath", localFile.getAbsolutePath()).set("filename", file.getOriginalFilename());
        return new Result<>().success(result);
    }

    @PostMapping("uploadExcelToTemp")
    @Operation(summary = "上传Excel到临时文件(文件形式)", description = "不支持分布式环境")
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

    @PostMapping("uploadBase64")
    @Operation(summary = "上传单文件(base64)")
    public Result<FileUuidItem> uploadBase64(@Validated @RequestBody OssFileBase64UploadReq req) {
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(req.getParamsCode(), OssPropsConfig.class, null);
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");
        // 有两个前缀，config定义前缀和用户上传前缀
        String objectKey = uploadService.buildObjectKey(req.getPrefix(), req.getFilaName());
        ApiResult<JSONObject> uploadResult = uploadService.uploadBase64(objectKey, req.getFileBase64());
        AssertUtils.isFalse(uploadResult.isSuccess(), uploadResult.getCodeMsg());
        FileUuidItem result = new FileUuidItem().setUrl(ossConfig.getDomain() + objectKey).setName(req.getFilaName());
        if (ossConfig.getSaveDb()) {
            //保存文件信息
            OssEntity oss = new OssEntity();
            oss.setUrl(ossConfig.getDomain() + objectKey);
            oss.setFilename(req.getFilaName());
            oss.setSize(0L);
            oss.setContentType(FileUtil.getMimeType(req.getFilaName()));
            oss.setType(req.getPrefix());
            oss.setPath(objectKey);
            oss.setUuid(IdUtil.fastUUID());
            ossService.save(oss);
            result.setUuid(oss.getUuid());
        }
        return new Result<FileUuidItem>().success(result);
    }

    @PostMapping("uploadMulti")
    @Operation(summary = "上传多文件")
    public Result<List<FileUuidItem>> uploadMulti(@RequestParam(required = false, defaultValue = SysConst.OSS_PUBLIC) String paramsCode,
                                 @RequestParam(required = false) String prefix,
                                 @RequestPart @NotEmpty(message = "文件不能为空") MultipartFile[] files) {
        List<FileUuidItem> resList = new ArrayList<>();
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(paramsCode, OssPropsConfig.class, null);
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");
        // 有两个前缀，config定义前缀和用户上传前缀
        for (MultipartFile file : files) {
            // 上传文件
            String objectKey = uploadService.buildObjectKey(prefix, file.getOriginalFilename());
            ApiResult<JSONObject> uploadResult = uploadService.upload(objectKey, file);
            if (uploadResult.isSuccess()) {
                FileUuidItem result = new FileUuidItem().setUrl(ossConfig.getDomain() + objectKey).setName(file.getOriginalFilename());
                if (ossConfig.getSaveDb()) {
                    //保存文件信息
                    OssEntity oss = new OssEntity();
                    oss.setUrl(ossConfig.getDomain() + objectKey);
                    oss.setFilename(file.getOriginalFilename());
                    oss.setSize(file.getSize());
                    oss.setContentType(file.getContentType());
                    ossService.save(oss);
                    result.setUuid(oss.getUuid());
                }
                resList.add(result);
            }
        }

        return new Result<List<FileUuidItem>>().success(resList);
    }

    @PostMapping("aliyunUploadCallback")
    @Operation(summary = "阿里云上传回调")
    @AccessControl
    public Result<?> aliyunUploadCallback(@Validated @RequestBody AliyunOssUploadCallbackReq req) {
        // todo 处理回调请求结果
        return new Result<>();
    }

    @PostMapping("getSignedPostForm")
    @Operation(summary = "获得已签名的post表单参数")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:oss"}, logical = Logical.OR)
    public Result<?> getSignedPostForm(@Validated @RequestBody OssSignedPostReq form) {
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(form.getParamsCode(), OssPropsConfig.class, null);
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        ApiResult<JSONObject> result = uploadService.getSignedPostForm(form.getConditions(), form.getExpire(), null);
        AssertUtils.isFalse(result.isSuccess(), result.getCodeMsg());
        return new Result<>().success(result.getData());
    }

    @PostMapping("getPreSignedPostForm")
    @Operation(summary = "获得已签名的post表单参数")
    public Result<?> getPreSignedPostForm(@Validated @RequestBody OssPreSignedReq form) {
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(StrUtil.emptyToDefault(form.getParamsCode(), SysConst.OSS_PUBLIC), OssPropsConfig.class, null);
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        String objectKey = uploadService.buildObjectKey(form.getPrefix(), form.getFileName());
        ApiResult<JSONObject> result = uploadService.getSignedPostForm(null, form.getExpire(), objectKey);
        AssertUtils.isFalse(result.isSuccess(), result.getCodeMsg());
        return new Result<>().success(result.getData());
    }

    @PostMapping("getPreSignedUrl")
    @Operation(summary = "获得授权访问地址")
    public Result<?> getPreSignedUrl(@Validated @RequestBody OssPreSignedReq form) {
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(form.getParamsCode(), OssPropsConfig.class, null);
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        String objectKey = uploadService.buildObjectKey(form.getPrefix(), form.getFileName());
        ApiResult<String> result = uploadService.getPreSignedUrl(objectKey, form.getMethod(), form.getExpire());
        AssertUtils.isFalse(result.isSuccess(), result.getCodeMsg());
        return new Result<>().success(result.getData());
    }

    @PostMapping("page")
    @Operation(summary = "分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:oss", "sys:oss:query"}, logical = Logical.OR)
    public Result<PageData<OssDTO>> page(@Validated({PageGroup.class}) @RequestBody OssQueryReq form) {
        PageData<OssDTO> page = ossService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<PageData<OssDTO>>().success(page);
    }

    @PostMapping("deleteBatch")
    @Operation(summary = "批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:oss", "sys:oss:delete"}, logical = Logical.OR)
    public Result<?> deleteBatch(@Validated @RequestBody IdsReq req) {
        ossService.remove(QueryWrapperHelper.getPredicate(req));

        return new Result<>();
    }

}
