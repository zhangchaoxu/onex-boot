package com.nb6868.onex.uc.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.PasswordUtils;
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

import java.util.Collections;

@RestController("UcProfile")
@RequestMapping("/uc/profile/")
@Validated
@Tag(name = "用户资料(我的)")
@Slf4j
public class ProfileController {

    @Autowired
    AuthProps authProps;
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

    @PostMapping("userInfo")
    @Operation(summary = "用户信息")
    public Result<UserDTO> userInfo(@Validated @RequestBody UserMyInfoReq req) {
        UserEntity user = userService.getById(ShiroUtils.getUserId());
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);
        UserDTO data = ConvertUtils.sourceToTarget(user, UserDTO.class);
        // 需要部门
        if (ObjUtil.equal(req.getDeptNeeded(), Const.BooleanEnum.TRUE.getCode())) {
            data.setDeptList(CollStreamUtil.toList(deptService.getDeptListByUserId(data.getId(), UcConst.DeptUserTypeEnum.DEFAULT.getCode()), entity -> BeanUtil.copyProperties(entity, DeptRes.class)));
        }
        // 需要角色
        if (ObjUtil.equal(req.getRoleNeeded(), Const.BooleanEnum.TRUE.getCode())) {
            data.setRoleList(CollStreamUtil.toList(roleService.getRoleListByUserId(data.getId()), entity -> BeanUtil.copyProperties(entity, RoleRes.class)));
        }
        return new Result<UserDTO>().success(data);
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
    public Result<?> updateUserPassword(@Validated @RequestBody UserUpdateMyPasswordReq form) {
        // 获得对应登录类型的登录参数
        JSONObject loginParams = paramsService.getSystemPropsJson(form.getType());
        AssertUtils.isNull(loginParams, "缺少[" + form.getType() + "]登录配置");
        // 先对密码做解密
        String passwordPlaintext = PasswordUtils.aesDecode(form.getPasswordEncrypted(), StrUtil.emptyToDefault(authProps.getTransferKey(), Const.AES_KEY));
        String newPasswordPlaintext = PasswordUtils.aesDecode(form.getNewPasswordEncrypted(), StrUtil.emptyToDefault(authProps.getTransferKey(), Const.AES_KEY));
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

}
