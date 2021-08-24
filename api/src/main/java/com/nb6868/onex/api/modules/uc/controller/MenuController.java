package com.nb6868.onex.api.modules.uc.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.api.common.annotation.LogOperation;
import com.nb6868.onex.api.modules.uc.UcConst;
import com.nb6868.onex.api.modules.uc.dto.MenuDTO;
import com.nb6868.onex.api.modules.uc.dto.MenuTreeDTO;
import com.nb6868.onex.api.modules.uc.entity.MenuEntity;
import com.nb6868.onex.api.modules.uc.service.AuthService;
import com.nb6868.onex.api.modules.uc.service.MenuScopeService;
import com.nb6868.onex.api.modules.uc.service.MenuService;
import com.nb6868.onex.api.modules.uc.user.SecurityUser;
import com.nb6868.onex.api.modules.uc.user.UserDetail;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.TreeUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import io.swagger.annotations.Api;
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
@RequestMapping("/uc/menu")
@Validated
@Api(tags = "菜单权限")
public class MenuController {

    @Autowired
    MenuService menuService;
    @Autowired
    MenuScopeService menuScopeService;
    @Autowired
    AuthService authService;

    @GetMapping("scope")
    @ApiOperation("权限范围")
    public Result<?> scope() {
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
            if (StrUtil.isNotBlank(menu.getUrl())) {
                urlList.add(ConvertUtils.sourceToTarget(menu, MenuDTO.class));
            }
            if (StrUtil.isNotBlank(menu.getPermissions())) {
                permissions.addAll(StrSplitter.splitTrim(menu.getPermissions(), ',', true));
            }
        });
        // 将菜单列表转成菜单树
        List<MenuTreeDTO> menuTree = TreeUtils.build(menuList);
        // 获取角色列表
        Set<String> roles = authService.getUserRoles(user);
        return new Result<>().success(Dict.create()
                .set("menuTree", menuTree)
                .set("urlList", urlList)
                .set("permissions", permissions)
                .set("roles", roles));
    }

    @GetMapping("tree")
    @ApiOperation("登录用户菜单树")
    public Result<?> tree(@RequestParam(required = false, name = "菜单类型 0：菜单 1：按钮  null：全部") Integer type) {
        UserDetail user = SecurityUser.getUser();
        List<MenuEntity> entityList = menuService.getListByUser(user, type);
        List<MenuTreeDTO> dtoList = ConvertUtils.sourceToTarget(entityList, MenuTreeDTO.class);
        List<MenuTreeDTO> dtoTree = TreeUtils.build(dtoList);

        return new Result<>().success(dtoTree);
    }

    @GetMapping("permissions")
    @ApiOperation("登录用户权限范围")
    public Result<?> permissions() {
        UserDetail user = SecurityUser.getUser();
        Set<String> set = authService.getUserPermissions(user);

        return new Result<>().success(set);
    }

    @GetMapping("roles")
    @ApiOperation("登录用户角色范围")
    public Result<?> roles() {
        UserDetail user = SecurityUser.getUser();
        Set<String> set = authService.getUserRoles(user);

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
        // 获取级联菜单id，包括自身和子菜单
        List<Long> menuIds = menuService.getCascadeChildrenListByIds(Collections.singletonList(id));
        // 然后删除
        boolean result = menuService.logicDeleteByIds(menuIds);

        return new Result<>();
    }

}
