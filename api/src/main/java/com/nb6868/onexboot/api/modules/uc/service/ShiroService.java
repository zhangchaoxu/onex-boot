package com.nb6868.onexboot.api.modules.uc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.common.config.LoginProps;
import com.nb6868.onexboot.api.common.config.LoginPropsSource;
import com.nb6868.onexboot.api.common.config.OnexProps;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.dingtalk.DingtalkScanProps;
import com.nb6868.onexboot.api.modules.uc.entity.MenuEntity;
import com.nb6868.onexboot.api.modules.uc.entity.TokenEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import com.nb6868.onexboot.api.modules.uc.wx.WxScanProps;
import com.nb6868.onexboot.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * shiro相关接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ShiroService {

    @Autowired
    private OnexProps onexProps;
    @Autowired
    private MenuService menuService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ParamService paramService;
    @Autowired
    private MenuScopeService menuScopeService;

    /**
     * 获取用户权限列表
     */
    public Set<String> getUserPermissions(UserDetail user) {
        // 系统管理员，拥有最高权限
        List<String> permissionsList;
        if (user.getType() == UcConst.UserTypeEnum.ADMIN.value()) {
            permissionsList = menuService.listObjs(new QueryWrapper<MenuEntity>().select("permissions").ne("permissions", ""), Object::toString);
        } else {
            permissionsList = menuScopeService.getPermissionsListByUserId(user.getId());
        }

        // 用户权限列表
        Set<String> set = new HashSet<>();
        for (String permissions : permissionsList) {
            if (StringUtils.isBlank(permissions)) {
                continue;
            }
            set.addAll(StringUtils.splitToList(permissions));
        }
        return set;
    }

    /**
     * 获取用户角色列表
     */
    public Set<String> getUserRoles(UserDetail user) {
        List<Long> roleList = user.getType() == UcConst.UserTypeEnum.ADMIN.value() ? roleService.getRoleIdList() : roleService.getRoleIdListByUserId(user.getId());
        // 用户角色列表
        Set<String> set = new HashSet<>();
        for (Long role : roleList) {
            set.add(String.valueOf(role));
        }
        return set;
    }

    /**
     * 通过token获取用户id
     */
    public TokenEntity getUserIdAndTypeByToken(String token) {
        return tokenService.getUserIdAndTypeByToken(token);
    }

    /**
     * 续token的过期时间
     *
     * @param token  token
     * @param expire 续期时间
     */
    public boolean renewalToken(String token, Long expire) {
        return tokenService.renewalToken(token, expire);
    }

    /**
     * 根据用户ID，查询用户
     *
     * @param userId 用户id
     */
    public UserEntity getUser(Long userId) {
        return userService.getById(userId);
    }

    /**
     * 通过登录类型获取登录配置
     *
     * @param type 类型
     * @return 登录配置
     */
    public LoginProps getLoginProps(String type) {
        if (UcConst.LoginTypeEnum.ADMIN_USERNAME_PASSWORD.name().equalsIgnoreCase(type)) {
            return onexProps.getLoginAdminProps().getUsernamePasswordLoginProps();
        }
        if (UcConst.LoginTypeEnum.ADMIN_MOBILE_SMSCODE.name().equalsIgnoreCase(type)) {
            return onexProps.getLoginAdminProps().getMobileSmscodeLoginProps();
        }
        if (UcConst.LoginTypeEnum.ADMIN_DINGTALK_SCAN.name().equalsIgnoreCase(type)) {
            return onexProps.getLoginAdminProps().getDingtalkScanLoginProps();
        }
        if (UcConst.LoginTypeEnum.ADMIN_WECHAT_SCAN.name().equalsIgnoreCase(type)) {
            return onexProps.getLoginAdminProps().getWechatScanLoginProps();
        }
        return paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + type, LoginProps.class);
    }

    /**
     * 获得后台登录配置
     */
    public OnexProps.LoginAdminProps getLoginAdminProps() {
        OnexProps.LoginAdminProps loginAdminProps = onexProps.getLoginAdminProps();
        if (loginAdminProps.getSource() == LoginPropsSource.DB) {
            loginAdminProps = paramService.getContentObject(UcConst.LOGIN_ADMIN, OnexProps.LoginAdminProps.class);
        }
        return loginAdminProps;
    }

    /**
     * 获得详细的后台登录配置
     */
    public OnexProps.LoginAdminProps getLoginAdminDetailProps() {
        OnexProps.LoginAdminProps loginAdminProps = getLoginAdminProps();
        // 各个登录途径的登录配置,需要确认是否从db读取
        if (loginAdminProps.isUsernamePasswordLogin() && loginAdminProps.getUsernamePasswordLoginProps().getSource() == LoginPropsSource.DB) {
            loginAdminProps.setUsernamePasswordLoginProps(paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_USERNAME_PASSWORD.name(), LoginProps.class));
        }
        if (loginAdminProps.isMobileSmscodeLogin() && loginAdminProps.getMobileSmscodeLoginProps().getSource() == LoginPropsSource.DB) {
            loginAdminProps.setMobileSmscodeLoginProps(paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_MOBILE_SMSCODE.name(), LoginProps.class));
        }
        if (loginAdminProps.isWechatScanLogin() && loginAdminProps.getWechatScanLoginProps().getSource() == LoginPropsSource.DB) {
            loginAdminProps.setWechatScanLoginProps(paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_WECHAT_SCAN.name(), LoginProps.class));
            loginAdminProps.setWechatScanProps(paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_WECHAT_SCAN.name() + "_CONFIG", WxScanProps.class));
        }
        if (loginAdminProps.isDingtalkScanLogin() && loginAdminProps.getDingtalkScanLoginProps().getSource() == LoginPropsSource.DB) {
            loginAdminProps.setDingtalkScanLoginProps(paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_DINGTALK_SCAN.name(), LoginProps.class));
            loginAdminProps.setDingtalkScanProps(paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_DINGTALK_SCAN.name() + "_CONFIG", DingtalkScanProps.class));
        }
        return loginAdminProps;
    }

}
