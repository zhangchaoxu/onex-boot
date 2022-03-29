package com.nb6868.onex.uc.controller;

import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.portal.modules.uc.dto.TenantDTO;
import com.nb6868.onex.portal.modules.uc.service.TenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/uc/tenant")
@Validated
@Api(tags = "租户")
public class TenantController {
    @Autowired
    private TenantService tenantService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("uc:tenant:info")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<TenantDTO> list = tenantService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("uc:tenant:info")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<TenantDTO> page = tenantService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("uc:tenant:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        TenantDTO data = tenantService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<TenantDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("uc:tenant:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody TenantDTO dto) {
        tenantService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:tenant:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody TenantDTO dto) {
        tenantService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("uc:tenant:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        tenantService.logicDeleteById(id);

        return new Result<>();
    }

}
