package com.nb6868.onex.cms.controller;

import com.nb6868.onex.cms.dto.SiteDTO;
import com.nb6868.onex.cms.dto.SiteQueryForm;
import com.nb6868.onex.cms.service.SiteService;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdTenantForm;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 站点
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/cms/site")
@Validated
@Api(tags="站点")
public class SiteController {

    @Autowired
    private SiteService siteService;

    @PostMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("cms:site:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> list(@Validated @RequestBody SiteQueryForm form) {
        List<?> list = siteService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<>().success(list);
    }

    @PostMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("cms:site:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody SiteQueryForm form) {
        PageData<?> page = siteService.pageDto(form.getPage(), QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("info")
    @ApiOperation("信息")
    @LogOperation("信息")
    @RequiresPermissions("cms:site:query")
    public Result<?> info(@Validated @RequestBody IdTenantForm form) {
        SiteDTO data = siteService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("cms:site:edit")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody SiteDTO dto) {
        siteService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("cms:site:edit")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody SiteDTO dto) {
        siteService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("cms:site:delete")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> delete(@Validated @RequestBody IdTenantForm form) {
        siteService.logicDeleteByWrapper(QueryWrapperHelper.getPredicate(form));

        return new Result<>();
    }

}
