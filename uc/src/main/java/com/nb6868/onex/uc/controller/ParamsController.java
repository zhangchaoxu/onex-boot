package com.nb6868.onex.uc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dto.ParamsDTO;
import com.nb6868.onex.uc.dto.ParamsInfoQueryForm;
import com.nb6868.onex.uc.dto.ParamsQueryForm;
import com.nb6868.onex.uc.entity.ParamsEntity;
import com.nb6868.onex.uc.service.ParamsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/uc/params/")
@Validated
@Api(tags = "参数管理", position = 25)
public class ParamsController {

    @Autowired
    private ParamsService paramsService;

    @PostMapping("infoByCode")
    @AccessControl
    @ApiOperation(value = "通过编码获取配置信息")
    @ApiOperationSupport(order = 5)
    public Result<?> infoByCode(@Validated @RequestBody ParamsInfoQueryForm form) {
        QueryWrapper<ParamsEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        ParamsEntity data = paramsService.getOne(queryWrapper);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        AssertUtils.isFalse(UcConst.ParamsScopeEnum.PUBLIC.value().equalsIgnoreCase(data.getScope()), "参数非公开");
        return new Result<>().success(JacksonUtils.jsonToNode(data.getContent()));
    }

    @PostMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:params:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @ApiOperationSupport(order = 8)
    public Result<?> list(@RequestBody ParamsQueryForm form) {
        List<?> list = paramsService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<>().success(list);
    }

    @PostMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:params:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @ApiOperationSupport(order = 10)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody ParamsQueryForm form) {
        PageData<?> page = paramsService.pageDto(form.getPage(), QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:params:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @ApiOperationSupport(order = 20)
    public Result<?> info(@Validated @RequestBody IdForm form) {
        ParamsDTO data = paramsService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:params:edit"}, logical = Logical.OR)
    @ApiOperationSupport(order = 40)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ParamsDTO dto) {
        paramsService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:params:edit"}, logical = Logical.OR)
    @ApiOperationSupport(order = 50)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ParamsDTO dto) {
        paramsService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:params:delete"}, logical = Logical.OR)
    @ApiOperationSupport(order = 60)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> delete(@Validated @RequestBody IdForm form) {
        paramsService.removeById(form.getId());
        return new Result<>();
    }

}
