package com.nb6868.onex.cms.controller;

import com.nb6868.onex.cms.dto.AxdDTO;
import com.nb6868.onex.cms.service.AxdService;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 广告位
 * axd是因为ad会被部分广告拦截的浏览器插件拦截掉
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping( "/cms/axd")
@Validated
@Api(tags="广告位")
public class AxdController {

    @Autowired
    private AxdService axdService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("cms:axd:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<AxdDTO> list = axdService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("cms:axd:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<AxdDTO> page = axdService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("cms:axd:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        AxdDTO data = axdService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("cms:axd:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody AxdDTO dto) {
        axdService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("cms:axd:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody AxdDTO dto) {
        axdService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("cms:axd:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        axdService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("cms:axd:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        axdService.logicDeleteByIds(ids);

        return new Result<>();
    }

}
