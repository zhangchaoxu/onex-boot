package com.nb6868.onex.uc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.uc.dto.TenantDTO;
import com.nb6868.onex.uc.dto.TenantQueryForm;
import com.nb6868.onex.uc.entity.TenantEntity;
import com.nb6868.onex.uc.service.TenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/uc/tenant")
@Validated
@Api(tags = "租户管理", position = 10)
public class TenantController {
    @Autowired
    private TenantService tenantService;

    @PostMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("uc:tenant:query")
    @ApiOperationSupport(order = 10)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody TenantQueryForm form) {
        QueryWrapper<TenantEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "page");
        PageData<?> page = tenantService.pageDto(form.getPage(), queryWrapper);

        return new Result<>().success(page);
    }

    @PostMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("uc:tenant:query")
    @ApiOperationSupport(order = 20)
    public Result<?> list(@Validated @RequestBody TenantQueryForm form) {
        QueryWrapper<TenantEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        List<?> list = tenantService.listDto(queryWrapper);

        return new Result<>().success(list);
    }

    @PostMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("uc:tenant:query")
    @ApiOperationSupport(order = 30)
    public Result<?> info(@Validated @RequestBody IdForm form) {
        TenantDTO data = tenantService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("uc:tenant:edit")
    @ApiOperationSupport(order = 40)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody TenantDTO dto) {
        tenantService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:tenant:edit")
    @ApiOperationSupport(order = 50)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody TenantDTO dto) {
        tenantService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("uc:tenant:delete")
    @ApiOperationSupport(order = 100)
    public Result<?> delete(@Validated @RequestBody IdForm form) {
        tenantService.logicDeleteByWrapper(QueryWrapperHelper.getPredicate(form));
        // 按业务需求做其它操作
        return new Result<>();
    }

}
