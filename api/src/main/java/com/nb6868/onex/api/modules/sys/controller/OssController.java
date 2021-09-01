package com.nb6868.onex.api.modules.sys.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.api.modules.sys.dto.OssDTO;
import com.nb6868.onex.api.modules.sys.entity.OssEntity;
import com.nb6868.onex.api.modules.sys.service.OssService;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.oss.AbstractOssService;
import com.nb6868.onex.common.oss.OssPropsConfig;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.MultipartFileUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 素材库
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/sys/oss")
@Validated
@Api(tags = "素材库")
public class OssController {

    @Autowired
    private OssService ossService;

    @GetMapping("page")
    @ApiOperation(value = "分页")
    @RequiresPermissions("sys:oss:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<OssDTO> page = ossService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("presignedUrl")
    @ApiOperation(value = "获得授权访问地址")
    public Result<?> presignedUrl(@RequestParam(required = false, defaultValue = "OSS_PRIVATE") String paramCode,
                                  @RequestParam String objectName,
                                  @RequestParam(required = false, defaultValue = "36000") long expiration) {
        String url = OssPropsConfig.getService(paramCode).generatePresignedUrl(objectName, expiration);

        return new Result<>().success(url);
    }

    @GetMapping("getSts")
    @ApiOperation(value = "获得STS临时访问token")
    public Result<?> getSts(@RequestParam(required = false, defaultValue = "OSS_PRIVATE") String paramCode) {
        Dict dict = OssPropsConfig.getService(paramCode).getSts();
        return new Result<>().success(dict);
    }

    @PostMapping("upload")
    @ApiOperation(value = "上传单文件(文件形式)")
    public Result<?> upload(@RequestParam(required = false, defaultValue = "OSS_PUBLIC", name = "OSS配置参数") String paramCode,
                            @RequestParam("file") MultipartFile file,
                            @RequestParam(required = false, name = "路径前缀") String prefix) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);

        // 上传文件
        String url = OssPropsConfig.getService(paramCode).upload(prefix, file);
        //保存文件信息
        OssEntity oss = new OssEntity();
        oss.setUrl(url);
        oss.setFilename(file.getOriginalFilename());
        oss.setSize(file.getSize());
        oss.setContentType(file.getContentType());
        ossService.save(oss);

        return new Result<>().success(Dict.create().set("src", url).set("oss", oss));
    }

    @PostMapping("uploadBase64")
    @ApiOperation(value = "上传单文件(base64形式)")
    public Result<?> uploadBase64(@RequestParam(required = false, defaultValue = "OSS_PUBLIC", name = "OSS配置参数") String paramCode,
                                  @RequestParam(name = "文件base64") String fileBase64,
                                  @RequestParam(required = false, name = "路径前缀") String prefix) {
        // 将base64转成file
        MultipartFile file = MultipartFileUtils.base64ToMultipartFile(fileBase64);
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);

        // 上传文件
        String url = OssPropsConfig.getService(paramCode).upload(prefix, file);
        //保存文件信息
        OssEntity oss = new OssEntity();
        oss.setUrl(url);
        oss.setFilename(file.getOriginalFilename());
        oss.setSize(file.getSize());
        oss.setContentType(file.getContentType());
        ossService.save(oss);

        return new Result<>().success(Dict.create().set("src", url).set("oss", oss));
    }

    @PostMapping("uploadMulti")
    @ApiOperation(value = "上传多文件")
    public Result<?> uploadMulti(@RequestParam(required = false, defaultValue = "OSS_PUBLIC", name = "OSS配置参数") String paramCode,
                                 @RequestParam("file") @NotEmpty(message = "文件不能为空") MultipartFile[] files,
                                 @RequestParam(required = false, name = "路径前缀") String prefix) {
        List<String> srcList = new ArrayList<>();
        List<OssEntity> ossList = new ArrayList<>();
        AbstractOssService abstractOssService = OssPropsConfig.getService(paramCode);
        for (MultipartFile file : files) {
            // 上传文件
            String url = abstractOssService.upload(prefix, file);
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

        return new Result<>().success(Dict.create().set("src", CollUtil.join(srcList, ",")).set("oss", ossList));
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:oss:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        ossService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:oss:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        ossService.logicDeleteByIds(ids);

        return new Result<>();
    }

}
