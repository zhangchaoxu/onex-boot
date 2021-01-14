package com.nb6868.onexboot.api.modules.uc.controller;

import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.dto.MenuDTO;
import com.nb6868.onexboot.api.modules.uc.dto.MenuTreeDTO;
import com.nb6868.onexboot.api.modules.uc.entity.MenuEntity;
import com.nb6868.onexboot.api.modules.uc.service.MenuService;
import com.nb6868.onexboot.api.modules.uc.service.RoleMenuService;
import com.nb6868.onexboot.api.modules.uc.service.ShiroService;
import com.nb6868.onexboot.api.modules.uc.user.SecurityUser;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.Kv;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.util.StringUtils;
import com.nb6868.onexboot.common.util.TreeUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.common.validator.group.AddGroup;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import com.nb6868.onexboot.common.validator.group.UpdateGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * 菜单权限
 * 注意这不仅仅是menu，还是permissions
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("uc/menu")
@Validated
@Api(tags = "菜单权限")
public class MenuController {

    @Autowired
    MenuService menuService;
    @Autowired
    RoleMenuService roleMenuService;

    @Autowired
    private ShiroService shiroService;

    @GetMapping("userMenu")
    @ApiOperation("用户菜单权限")
    public Result<?> userMenu() {
        UserDetail user = SecurityUser.getUser();
        // 获取该用户所有menu
        List<MenuEntity> allList = menuService.getListByUser(user, null);
        // 过滤出其中显示菜单
        List<MenuTreeDTO> menuList = new ArrayList<>();
        // 过滤出其中路由菜单
        List<MenuDTO> urlList = new ArrayList<>();
        // 过滤出其中的权限
        Set<String> permissions = new HashSet<>();
        allList.forEach(menu -> {
            if (menu.getShowMenu() == 1 && menu.getType() == UcConst.MenuTypeEnum.MENU.value()) {
                menuList.add(ConvertUtils.sourceToTarget(menu, MenuTreeDTO.class));
            }
            if (StringUtils.isNotBlank(menu.getUrl())) {
                urlList.add(ConvertUtils.sourceToTarget(menu, MenuDTO.class));
            }
            if (StringUtils.isNotBlank(menu.getPermissions())) {
                permissions.addAll(StringUtils.splitToList(menu.getPermissions()));
            }
        });
        // 将菜单列表转成菜单树
        List<MenuTreeDTO> menuTree = TreeUtils.build(menuList);
        // 获取角色列表
        Set<String> roles = shiroService.getUserRoles(user);
        return new Result<>().success(Kv.init()
                .set("menuTree", menuTree)
                .set("urlList", urlList)
                .set("roles", roles)
                .set("permissions", permissions));
    }

    @GetMapping("userTree")
    @ApiOperation("当前用户菜单")
    @ApiImplicitParam(name = "type", value = "菜单类型 0：菜单 1：按钮  null：全部", paramType = "query", dataType = "int")
    public Result<?> userTree(Integer type) {
        UserDetail user = SecurityUser.getUser();
        List<MenuTreeDTO> list = menuService.getTreeByUser(user, type);

        return new Result<>().success(list);
    }

    @GetMapping("permissions")
    @ApiOperation("登录用户权限范围")
    public Result<?> permissions() {
        UserDetail user = SecurityUser.getUser();
        Set<String> set = shiroService.getUserPermissions(user);

        return new Result<Set<String>>().success(set);
    }

    @GetMapping("roles")
    @ApiOperation("登录用户权限范围")
    public Result<?> roles() {
        UserDetail user = SecurityUser.getUser();
        Set<String> set = shiroService.getUserRoles(user);

        return new Result<Set<String>>().success(set);
    }

    @GetMapping("tree")
    @ApiOperation("列表")
    @ApiImplicitParam(name = "type", value = "菜单类型 0：菜单 1：按钮  null：全部", paramType = "query", dataType = "int")
    @RequiresPermissions("uc:menu:list")
    public Result<?> tree(Integer type) {
        List<MenuTreeDTO> list = menuService.getTreeByType(type);

        return new Result<>().success(list);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("uc:menu:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        MenuDTO data = menuService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

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

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:menu:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody MenuDTO dto) {
        menuService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("uc:menu:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        // 包括自己和子菜单
        List<Long> menuIds = menuService.getCascadeChildrenListByIds(Collections.singletonList(id));
        menuService.logicDeleteByIds(menuIds);

        return new Result<>();
    }

}
