package com.nb6868.onex.uc.controller;

import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.TreeUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.uc.dto.MenuDTO;
import com.nb6868.onex.uc.dto.MenuTreeDTO;
import com.nb6868.onex.uc.entity.MenuEntity;
import com.nb6868.onex.uc.service.MenuScopeService;
import com.nb6868.onex.uc.service.MenuService;
import com.nb6868.onex.uc.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
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
    private MenuScopeService menuScopeService;
    @Autowired
    private UserService userService;

    @GetMapping("tree")
    @ApiOperation("登录用户菜单树")
    public Result<?> tree(@RequestParam(required = false, name = "菜单类型 0：菜单 1：按钮  null：全部") Integer type) {
        ShiroUser user = ShiroUtils.getUser();
        List<MenuEntity> entityList = menuService.getListByUser(user, type);
        List<MenuTreeDTO> dtoList = ConvertUtils.sourceToTarget(entityList, MenuTreeDTO.class);
        List<MenuTreeDTO> dtoTree = TreeUtils.build(dtoList);

        return new Result<>().success(dtoTree);
    }

    @GetMapping("permissions")
    @ApiOperation("登录用户权限范围")
    public Result<?> permissions() {
        ShiroUser user = ShiroUtils.getUser();
        Set<String> set = userService.getUserPermissions(user);

        return new Result<>().success(set);
    }

    @GetMapping("roles")
    @ApiOperation("登录用户角色范围")
    public Result<?> roles() {
        ShiroUser user = ShiroUtils.getUser();
        Set<String> set = userService.getUserRoles(user);

        return new Result<>().success(set);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("uc:menu:info")
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
    @RequiresPermissions("uc:menu:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody MenuDTO dto) {
        menuService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:menu:update")
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
