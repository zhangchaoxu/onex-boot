package com.nb6868.onex.uc.controller;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.TreeNodeUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.uc.dto.DeptDTO;
import com.nb6868.onex.uc.dto.DeptQueryForm;
import com.nb6868.onex.uc.entity.DeptEntity;
import com.nb6868.onex.uc.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/uc/dept")
@Validated
@Tag(name = "部门管理")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @PostMapping("tree")
    @Operation(summary = "树表")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:dept:query"}, logical = Logical.OR)
    public Result<?> tree(@Validated @RequestBody DeptQueryForm form) {
        QueryWrapper<DeptEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        List<Tree<String>> treeList = TreeNodeUtils.buildCodeTree(
                CollStreamUtil.toList(deptService.list(queryWrapper),
                        (entity) -> new TreeNode<>(entity.getCode(), entity.getPcode(), entity.getName(), entity.getSort()).setExtra(Dict.create().set("type", entity.getType()))));
        return new Result<>().success(treeList);
    }

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:dept:query"}, logical = Logical.OR)
    public Result<?> list(@Validated @RequestBody DeptQueryForm form) {
        List<?> list = deptService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<>().success(list);
    }

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:dept:query"}, logical = Logical.OR)
    public Result<?> page(@Validated(PageGroup.class) @RequestBody DeptQueryForm form) {
        PageData<?> page = deptService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:dept:query"}, logical = Logical.OR)
    public Result<?> info(@Validated @RequestBody IdForm form) {
        DeptDTO data = deptService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    @LogOperation("保存")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:dept:edit"}, logical = Logical.OR)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody DeptDTO dto) {
        deptService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @Operation(summary = "修改")
    @LogOperation("修改")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:dept:edit"}, logical = Logical.OR)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody DeptDTO dto) {
        deptService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:dept:delete"}, logical = Logical.OR)
    public Result<?> delete(@Validated @RequestBody IdForm form) {
        deptService.removeById(form.getId());

        return new Result<>();
    }

}
