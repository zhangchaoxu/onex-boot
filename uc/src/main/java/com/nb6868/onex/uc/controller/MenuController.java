package com.nb6868.onex.uc.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdForm;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.TreeNodeUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.uc.dto.MenuDTO;
import com.nb6868.onex.uc.dto.MenuQueryForm;
import com.nb6868.onex.uc.entity.MenuEntity;
import com.nb6868.onex.uc.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限
 * 注意这不仅仅是menu，还是permissions
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/uc/menu")
@Validated
@Tag(name = "菜单权限")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @PostMapping("tree")
    @Operation(summary = "树列表", description = "按租户来,不做用户的权限区分")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:menu:query"}, logical = Logical.OR)
    public Result<?> tree(@Validated @RequestBody MenuQueryForm form) {
        QueryWrapper<MenuEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        List<TreeNode<Long>> menuList = new ArrayList<>();
        menuService.list(queryWrapper).forEach(menu -> menuList.add(new TreeNode<>(menu.getId(), menu.getPid(), menu.getName(), menu.getSort()).setExtra(Dict.create().set("permissions", menu.getPermissions()).set("type", menu.getType()).set("icon", menu.getIcon()).set("url", menu.getUrl()).set("urlNewBlank", menu.getUrlNewBlank()))));
        List<Tree<Long>> treeList = TreeNodeUtils.buildIdTree(menuList);
        return new Result<>().success(treeList);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:menu:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> info(@Validated @RequestBody IdForm form) {
        MenuDTO data = menuService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 赋值父菜单
        data.setParentMenuList(menuService.getParentList(data.getPid()));

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    @LogOperation("保存")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:menu:edit"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody MenuDTO dto) {
        menuService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @Operation(summary = "修改")
    @LogOperation("修改")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:menu:edit"}, logical = Logical.OR)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody MenuDTO dto) {
        menuService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:menu:delete"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> delete(@Validated @RequestBody IdForm form) {
        // 判断数据
        MenuEntity data = menuService.getOne(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 级联删除菜单以及下面所有子菜单
        menuService.deleteAllCascadeById(data.getId());
        return new Result<>();
    }

}
