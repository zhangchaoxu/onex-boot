package com.nb6868.onex.uc.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.util.*;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.msg.BaseMsgService;
import com.nb6868.onex.common.msg.MsgSendReq;
import com.nb6868.onex.common.msg.MsgTplBody;
import com.nb6868.onex.common.pojo.BaseReq;
import com.nb6868.onex.common.pojo.CodeReq;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.PasswordUtils;
import com.nb6868.onex.common.util.TreeNodeUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dto.*;
import com.nb6868.onex.uc.entity.ParamsEntity;
import com.nb6868.onex.uc.entity.UserEntity;
import com.nb6868.onex.uc.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController("UcProfile")
@RequestMapping("/uc/profile/")
@Validated
@Tag(name = "用户资料(我的)")
@Slf4j
public class ProfileController {

    @Autowired
    AuthProps authProps;
    @Autowired
    AuthService authService;
    @Autowired
    ParamsService paramsService;
    @Autowired
    UserService userService;
    @Autowired
    DeptService deptService;
    @Autowired
    RoleService roleService;
    @Autowired
    TokenService tokenService;
    @Autowired
    MenuService menuService;
    @Autowired
    BaseMsgService msgService;

    @PostMapping("userInfo")
    @Operation(summary = "用户信息")
    public Result<UserDTO> userInfo(@Validated @RequestBody UserMyInfoReq req) {
        UserEntity user = userService.getById(ShiroUtils.getUserId());
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);
        UserDTO data = ConvertUtils.sourceToTarget(user, UserDTO.class);
        // 需要部门
        data.setDeptList(req.isDeptNeeded() ? CollStreamUtil.toList(deptService.getDeptListByUserId(user.getId(), UcConst.DeptUserTypeEnum.DEFAULT.getCode()), entity -> BeanUtil.copyProperties(entity, DeptRes.class)) : CollUtil.newArrayList());
        // 需要角色
        data.setRoleList(req.isRoleNeeded() ? CollStreamUtil.toList(roleService.getRoleListByUserId(user.getId()), entity -> BeanUtil.copyProperties(entity, RoleRes.class)) : CollUtil.newArrayList());
        return new Result<UserDTO>().success(data);
    }

    @PostMapping("userMenuScope")
    @Operation(summary = "用户权限范围", description = "返回包括菜单、路由、权限、角色等所有内容")
    public Result<MenuScopeRes> userMenuScope(@Validated @RequestBody MenuScopeReq req) {
        ShiroUser user = ShiroUtils.getUser();
        // 过滤出其中显示菜单
        List<TreeNode<Long>> menuList = new ArrayList<>();
        // 过滤出其中路由菜单
        List<MenuRes> urlList = new ArrayList<>();
        // 过滤出其中的权限
        List<String> permissions = new ArrayList<>();
        // 获取该用户所有menu
        menuService.getListByUser(user.getType(), user.getTenantCode(), user.getId(), null, null).forEach(menu -> {
            if (menu.getShowMenu() == 1 && menu.getType() == UcConst.MenuTypeEnum.MENU.getCode()) {
                // 菜单需要显示 && 菜单类型为菜单
                menuList.add(new TreeNode<>(menu.getId(), menu.getPid(), menu.getName(), menu.getSort()).setExtra(Dict.create()
                        .set("component", menu.getComponent())
                        .set("meta", menu.getMeta())
                        .set("icon", menu.getIcon())
                        .set("url", menu.getUrl())
                        .set("urlNewBlank", menu.getUrlNewBlank())));
            }
            if (StrUtil.isNotBlank(menu.getUrl())) {
                urlList.add(ConvertUtils.sourceToTarget(menu, MenuRes.class));
            }
            if (req.isPermissions() && StrUtil.isNotBlank(menu.getPermissions())) {
                permissions.addAll(StrUtil.splitTrim(menu.getPermissions(), ','));
            }
        });
        MenuScopeRes result = new MenuScopeRes()
                // 将菜单列表转成菜单树
                .setMenuTree(TreeNodeUtils.buildIdTree(menuList))
                // 塞入权限,去重
                .setPermissions(CollUtil.distinct(permissions))
                // 塞入角色编码
                .setRoleCodes(req.isRoleCodes() ? userService.getUserRoleCodes(user) : CollUtil.newArrayList())
                // 塞入角色id
                .setRoleIds(req.isRoleIds() ? userService.getUserRoleIds(user) : CollUtil.newArrayList())
                .setUrlList(urlList);
        return new Result<MenuScopeRes>().success(result);
    }

    @PostMapping("userMenuTree")
    @Operation(summary = "用户菜单树", description = "用户左侧显示菜单")
    public Result<List<Tree<Long>>> userMenuTree(@Validated @RequestBody BaseReq req) {
        ShiroUser user = ShiroUtils.getUser();
        List<TreeNode<Long>> menuList = new ArrayList<>();
        // 获取该用户所有menu, 菜单需要显示 && 菜单类型为菜单
        menuService.getListByUser(user.getType(), user.getTenantCode(), user.getId(), UcConst.MenuTypeEnum.MENU.getCode(), 1)
                .forEach(menu -> menuList.add(new TreeNode<>(menu.getId(), menu.getPid(), menu.getName(), menu.getSort()).setExtra(Dict.create()
                        .set("component", menu.getComponent())
                        .set("meta", menu.getMeta())
                        .set("icon", menu.getIcon())
                        .set("url", menu.getUrl())
                        .set("urlNewBlank", menu.getUrlNewBlank()))));
        List<Tree<Long>> menuTree = TreeNodeUtils.buildIdTree(menuList);
        return new Result<List<Tree<Long>>>().success(menuTree);
    }

    @PostMapping("userPermissions")
    @Operation(summary = "用户授权编码", description = "用户具备的权限,可用于按钮等的控制")
    public Result<List<String>> userPermissions(@Validated @RequestBody BaseReq req) {
        ShiroUser user = ShiroUtils.getUser();
        List<String> set = userService.getUserPermissions(user);

        return new Result<List<String>>().success(set);
    }

    @PostMapping("userRoleIds")
    @Operation(summary = "用户角色id", description = "用户具备的角色,可用于按钮等的控制")
    public Result<List<Long>> userRoles(@Validated @RequestBody BaseReq req) {
        ShiroUser user = ShiroUtils.getUser();
        List<Long> set = userService.getUserRoleIds(user);

        return new Result<List<Long>>().success(set);
    }

    @PostMapping("userRoleCodes")
    @Operation(summary = "用户角色编码", description = "用户具备的角色,可用于按钮等的控制")
    public Result<List<String>> userRoleCodes(@Validated @RequestBody BaseReq req) {
        ShiroUser user = ShiroUtils.getUser();
        List<String> set = userService.getUserRoleCodes(user);

        return new Result<List<String>>().success(set);
    }

    @PostMapping("sendUserUpdateMobileCode")
    @Operation(summary = "发送绑定手机号验证码")
    @LogOperation("发送绑定手机号验证码")
    public Result<?> sendUserUpdateMobileCode(@Validated @RequestBody MsgSendReq req) {
        MsgTplBody mailTpl = msgService.getTplByCode(req.getTenantCode(), req.getTplCode());
        AssertUtils.isNull(mailTpl, ErrorCode.ERROR_REQUEST, "消息模板不存在");
        AssertUtils.isNull(mailTpl.getParams(), ErrorCode.ERROR_REQUEST, "消息模板未做参数配置");
        AssertUtils.isFalse(PhoneUtil.isMobile(req.getMailTo()), ErrorCode.ERROR_REQUEST, "手机号码格式错误");
        AssertUtils.isFalse(StrUtil.equalsIgnoreCase(req.getTplCode(), "USER_UPDATE_MOBILE"), ErrorCode.ERROR_REQUEST, "模板编码错误");
        // 验证验证码
        if (mailTpl.getParams().getBool("captcha", false)) {
            authService.checkCaptcha(req, mailTpl.getParams().getStr("magicCaptcha"));
        } else if (mailTpl.getParams().getBool("captchaAliyun", false)) {
            // 阿里云验证码
            JSONObject captchaParams = paramsService.getSystemPropsJson("SMS_CAPTCHA_ALIYUN");
            AssertUtils.isNull(captchaParams, "缺少阿里云验证码配置");
            authService.checkCaptchaAliyun(req, captchaParams);
        }
        // 检查手机号是不是自己的
        ShiroUser user = ShiroUtils.getUser();
        AssertUtils.isTrue(StrUtil.equalsIgnoreCase(user.getMobile(), req.getMailTo()), StrUtil.format("手机号[{}]已绑定当前用户,无法重复绑定",req.getMailTo()));
        // 检查手机号是不是已经被其它用户绑定
        AssertUtils.isTrue(userService.getByMobile(req.getTenantCode(), req.getMailTo()) != null, StrUtil.format("手机号[{}]已绑定其它用户,无法重复绑定",req.getMailTo()));
        boolean flag = msgService.sendMail(req);
        if (flag) {
            return new Result<>().success("短信发送成功", null);
        } else {
            return new Result<>().error("短信发送失败");
        }
    }

    @PostMapping("updateUserMobile")
    @Operation(summary = "更新用户手机号")
    @LogOperation(value = "更新用户手机号")
    public Result<?> updateUserMobile(@Validated @RequestBody ProfileUpdateUserMobileReq req) {
        // 这里要做二次检查，因为发送短信和更新会有时差
        // 检查手机号是不是自己的
        ShiroUser user = ShiroUtils.getUser();
        AssertUtils.isTrue(StrUtil.equalsIgnoreCase(user.getMobile(), req.getMobile()), StrUtil.format("手机号[{}]已绑定当前用户,无法重复绑定", req.getMobile()));
        // 检查手机号是不是已经被其它用户绑定
        AssertUtils.isTrue(userService.getByMobile(user.getTenantCode(), req.getMobile()) != null, StrUtil.format("手机号[{}]已绑定其它用户,无法重复绑定",req.getMobile()));
        // 验证并将短信消费掉
        msgService.verifyMailCode(user.getTenantCode(), "USER_UPDATE_MOBILE", req.getMobile(), req.getSms());
        // 先判断是否存在
        userService.lambdaUpdate()
                .eq(UserEntity::getId, user.getId())
                .set(UserEntity::getMobile, req.getMobile())
                .update(new UserEntity());
        return new Result<>();
    }

    @PostMapping("updateUserParams")
    @Operation(summary = "更新用户个人参数")
    @LogOperation(value = "更新用户个人参数")
    public Result<?> saveOrUpdateUserParams(@Validated @RequestBody ProfileParamsSaveOrUpdateReq req) {
        Long currentUserId = ShiroUtils.getUserId();
        // 先判断是否存在
        ParamsEntity entity = paramsService.lambdaQuery()
                .eq(ParamsEntity::getType, UcConst.ParamsTypeEnum.USER.getCode())
                .eq(ParamsEntity::getScope, UcConst.ParamsScopeEnum.PRIVATE.getCode())
                .eq(ParamsEntity::getUserId, currentUserId)
                .eq(ParamsEntity::getCode, req.getCode())
                .last(Const.LIMIT_ONE).one();
        entity = ObjUtil.defaultIfNull(entity, new ParamsEntity());
        entity.setType(UcConst.ParamsTypeEnum.USER.getCode());
        entity.setScope(UcConst.ParamsScopeEnum.PRIVATE.getCode());
        entity.setUserId(currentUserId);
        BeanUtil.copyProperties(req, entity);
        paramsService.saveOrUpdateById(entity);
        return new Result<>();
    }

    @PostMapping("updateUserPassword")
    @Operation(summary = "用户修改密码")
    @LogOperation("用户修改密码")
    public Result<?> updateUserPassword(@Validated @RequestBody UserUpdateMyPasswordReq req) {
        // 获得对应登录类型的登录参数
        JSONObject loginParams = paramsService.getSystemPropsJson(req.getType());
        AssertUtils.isNull(loginParams, "缺少[" + req.getType() + "]登录配置");
        // 先对密码做解密
        String passwordPlaintext = PasswordUtils.aesDecode(req.getPasswordEncrypted(), StrUtil.emptyToDefault(authProps.getTransferKey(), Const.AES_KEY));
        String newPasswordPlaintext = PasswordUtils.aesDecode(req.getNewPasswordEncrypted(), StrUtil.emptyToDefault(authProps.getTransferKey(), Const.AES_KEY));
        AssertUtils.isEmpty(passwordPlaintext, "原密码传输请做加密处理");
        AssertUtils.isEmpty(newPasswordPlaintext, "新密码传输请做加密处理");
        // 对新密码密码强度做校验
        // 密码复杂度正则
        AssertUtils.isTrue(StrUtil.isNotBlank(loginParams.getStr("passwordRegExp")) && !ReUtil.isMatch(loginParams.getStr("passwordRegExp"), newPasswordPlaintext), ErrorCode.ERROR_REQUEST, loginParams.getStr("passwordRegError", "密码不符合规则"));
        // 获取数据库中的用户
        UserEntity data = userService.getById(ShiroUtils.getUserId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 校验原密码
        AssertUtils.isFalse(PasswordUtils.verify(passwordPlaintext, data.getPassword()), ErrorCode.ACCOUNT_PASSWORD_ERROR);
        // 更新密码
        userService.updatePassword(data.getId(), newPasswordPlaintext, authProps.getPasswordStoreKey());
        // 注销该用户所有token,提示用户重新登录
        tokenService.deleteByUserIdList(Collections.singletonList(data.getId()));
        return new Result<>();
    }

    @PostMapping("getUserParamsContent")
    @Operation(summary = "获得用户个人参数内容")
    public Result<String> getUserParamsContent(@Validated @RequestBody ProfileParamQueryReq req) {
        Long currentUserId = ShiroUtils.getUserId();
        // 先判断是否存在
        ParamsEntity entity = paramsService.lambdaQuery()
                .eq(ParamsEntity::getType, UcConst.ParamsTypeEnum.USER.getCode())
                //.eq(ParamsEntity::getScope, UcConst.ParamsScopeEnum.PRIVATE.getCode())
                .eq(ParamsEntity::getUserId, currentUserId)
                .eq(ParamsEntity::getCode, req.getCode())
                .last(Const.LIMIT_ONE).one();
        return new Result<String>().success(entity == null ? null : entity.getContent());
    }

    @PostMapping("createOauthBindCode")
    @Operation(summary = "生成第三方平台绑定代码")
    @LogOperation(value = "生成第三方平台绑定代码")
    public Result<?> createOauthBindCode(@Validated @RequestBody CodeReq req) {
        JSONObject params = paramsService.getSystemPropsJson(req.getCode());
        String id = IdUtil.fastSimpleUUID();

        return new Result<>();
    }

}
