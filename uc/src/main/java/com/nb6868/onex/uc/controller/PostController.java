package com.nb6868.onex.uc.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
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
import com.nb6868.onex.uc.dto.PostDTO;
import com.nb6868.onex.uc.dto.PostQueryForm;
import com.nb6868.onex.uc.entity.PostEntity;
import com.nb6868.onex.uc.service.PostService;
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
@RequestMapping("/uc/post")
@Validated
@Api(tags = "岗位管理", position = 35)
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:post:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @ApiOperationSupport(order = 10)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody PostQueryForm form) {
        PageData<?> page = postService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:post:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @ApiOperationSupport(order = 20)
    public Result<?> list(@Validated @RequestBody PostQueryForm form) {
        List<?> list = postService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<>().success(list);
    }

    @PostMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:post:query"}, logical = Logical.OR)
    @ApiOperationSupport(order = 30)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> info(@Validated @RequestBody IdForm form) {
        PostDTO data = postService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:post:edit"}, logical = Logical.OR)
    @ApiOperationSupport(order = 40)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody PostDTO dto) {
        postService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:post:edit"}, logical = Logical.OR)
    @ApiOperationSupport(order = 50)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody PostDTO dto) {
        postService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:post:delete"}, logical = Logical.OR)
    @ApiOperationSupport(order = 60)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> delete(@Validated @RequestBody IdForm form) {
        // 判断数据是否存在
        PostEntity data = postService.getOne(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // todo 删除关联用户
        return new Result<>();
    }

}
