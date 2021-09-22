package com.nb6868.onex.shop.modules.uc.controller;

import cn.hutool.core.lang.Dict;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogLogin;
import com.nb6868.onex.common.auth.OauthWxMaLoginByCodeAndPhone;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.shop.modules.uc.dto.UserDTO;
import com.nb6868.onex.shop.modules.uc.entity.UserEntity;
import com.nb6868.onex.shop.modules.uc.service.UserService;
import com.nb6868.onex.shop.shiro.SecurityUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController("UcUserController")
@RequestMapping("/uc/user")
@Validated
@Api(tags = "用户")
@ApiSupport(order = 10)
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/wxMaLoginByPhone")
    @ApiOperation("微信小程序手机号授权登录")
    @LogLogin
    @AccessControl("/wxMaLoginByPhone")
    public Result<Dict> wxMaLoginByPhone(@Validated(value = {DefaultGroup.class}) @RequestBody OauthWxMaLoginByCodeAndPhone request) {
        /*AuthProps.Config loginProps = authService.getLoginConfig(request.getType());
        AssertUtils.isNull(loginProps, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 微信登录(小程序)
        WxMaService wxService = WechatMaPropsConfig.getService(request.getType());
        WxMaJscode2SessionResult jscode2SessionResult;
        try {
            jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());
        } catch (WxErrorException e) {
            log.error(e.getMessage());
            return new Result<Dict>().error("获取微信信息失败,Error=" + e.getError().getErrorCode());
        }
        // 解密用户手机号
        WxMaPhoneNumberInfo phoneNumberInfo = wxService.getUserService().getPhoneNoInfo(jscode2SessionResult.getSessionKey(), request.getEncryptedData(), request.getIv());
        UserEntity user = userService.getByMobile(phoneNumberInfo.getPurePhoneNumber());
        if (user == null) {
            // 用户不存在,按照实际业务需求创建用户
            user = new UserEntity();
            user.setMobile(phoneNumberInfo.getPurePhoneNumber());
            user.setUsername(phoneNumberInfo.getPurePhoneNumber());
            user.setPassword(DigestUtil.bcrypt(phoneNumberInfo.getPurePhoneNumber()));
            user.setState(UcConst.UserStateEnum.ENABLED.value());
            user.setType(UcConst.UserTypeEnum.USER.value());
            userService.save(user);
            // 插入对应用户角色
            roleUserService.saveOrUpdateByUserIdAndRoleIds(user.getId(), Collections.singletonList(UcConst.ROLE_ID_WECHAT));
        } else {
            // 更新用户中的部分信息
            // 更新角色信息
            roleUserService.addByUserIdAndRoleIds(user.getId(), Collections.singletonList(UcConst.ROLE_ID_WECHAT));
        }
        // 登录成功
        Dict dict = Dict.create();
        dict.set(UcConst.TOKEN_HEADER, tokenService.createToken(user, loginProps));
        dict.set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<Dict>().success(dict);*/
        return null;
    }

    @GetMapping("userInfo")
    @ApiOperation("登录用户信息")
    public Result<?> userInfo() {
        UserEntity entity = userService.getById(SecurityUser.getUserId());
        AssertUtils.isNull(entity, ErrorCode.DB_RECORD_EXISTS);
        // 转换成dto
        UserDTO dto = ConvertUtils.sourceToTarget(entity, UserDTO.class);
        return new Result<>().success(dto);
    }

}
