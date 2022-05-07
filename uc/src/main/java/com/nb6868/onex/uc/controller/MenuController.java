package com.nb6868.onex.uc.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.TreeNodeUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.uc.dto.MenuDTO;
import com.nb6868.onex.uc.dto.MenuQueryForm;
import com.nb6868.onex.uc.dto.RoleDTO;
import com.nb6868.onex.uc.entity.MenuEntity;
import com.nb6868.onex.uc.entity.RoleEntity;
import com.nb6868.onex.uc.service.MenuService;
import com.nb6868.onex.uc.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 菜单权限
 * 注意这不仅仅是menu，还是permissions
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/uc/menu")
@Validated
@Api(tags = "菜单权限", position = 50)
public class MenuController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private UserService userService;

    @PostMapping("tree")
    @ApiOperation(value = "树列表", notes = "按租户来,不做用户的权限区分")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("uc:menu:query")
    public Result<?> tree(@Validated @RequestBody MenuQueryForm form) {
        QueryWrapper<MenuEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        List<TreeNode<Long>> menuList = new ArrayList<>();
        menuService.list(queryWrapper).forEach(menu -> menuList.add(new TreeNode<>(menu.getId(), menu.getPid(), menu.getName(), menu.getSort()).setExtra(Dict.create().set("icon", menu.getIcon()).set("url", menu.getUrl()).set("urlNewBlank", menu.getUrlNewBlank()))));
        List<Tree<Long>> treeList = TreeNodeUtils.buildIdTree(menuList);
        return new Result<>().success(treeList);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("uc:menu:query")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        MenuDTO data = menuService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 赋值父菜单
        data.setParentMenuList(menuService.getParentList(data.getPid()));

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("uc:menu:edit")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody MenuDTO dto) {
        menuService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:menu:edit")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody MenuDTO dto) {
        menuService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("uc:menu:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        // 获取级联菜单id，包括自身和子菜单
        List<Long> menuIds = menuService.getCascadeChildrenListByIds(Collections.singletonList(id));
        // 然后删除
        boolean result = menuService.logicDeleteByIds(menuIds);

        return new Result<>();
    }

}
