package com.nb6868.onex.cms.controller;

import com.nb6868.onex.cms.dto.AxdDTO;
import com.nb6868.onex.cms.dto.AxdQueryForm;
import com.nb6868.onex.cms.service.AxdService;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdTenantForm;
import com.nb6868.onex.common.pojo.IdsTenantForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

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

    @PostMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("cms:axd:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> list(@Validated @RequestBody AxdQueryForm form) {
        List<?> list = axdService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<>().success(list);
    }

    @PostMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("cms:axd:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody AxdQueryForm form) {
        PageData<?> page = axdService.pageDto(form.getPage(), QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("cms:axd:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> info(@Validated @RequestBody IdTenantForm form) {
        AxdDTO data = axdService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("cms:axd:edit")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody AxdDTO dto) {
        axdService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("cms:axd:edit")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody AxdDTO dto) {
        axdService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("cms:axd:delete")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> delete(@Validated @RequestBody IdTenantForm form) {
        axdService.logicDeleteByWrapper(QueryWrapperHelper.getPredicate(form));

        return new Result<>();
    }

    @PostMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("cms:axd:delete")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> deleteBatch(@Validated @RequestBody IdsTenantForm form) {
        axdService.logicDeleteByWrapper(QueryWrapperHelper.getPredicate(form));

        return new Result<>();
    }

}
