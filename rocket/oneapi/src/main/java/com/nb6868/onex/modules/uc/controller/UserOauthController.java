package com.nb6868.onex.modules.uc.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.Kv;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.util.ConvertUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogLogin;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.modules.sys.service.ParamService;
import com.nb6868.onex.modules.uc.UcConst;
import com.nb6868.onex.modules.uc.dto.OauthLoginByCodeRequest;
import com.nb6868.onex.modules.uc.dto.OauthWxMaLoginByCodeAndUserInfoRequest;
import com.nb6868.onex.modules.uc.dto.UserDTO;
import com.nb6868.onex.modules.uc.dto.UserOauthDTO;
import com.nb6868.onex.modules.uc.entity.UserEntity;
import com.nb6868.onex.modules.uc.entity.UserOauthEntity;
import com.nb6868.onex.modules.uc.service.TokenService;
import com.nb6868.onex.modules.uc.service.UserOauthService;
import com.nb6868.onex.modules.uc.service.UserService;
import com.nb6868.onex.modules.uc.wx.WxApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 第三方用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("UserOauth")
@RequestMapping("uc/userOauth")
@Validated
@Api(tags = "第三方用户")
public class UserOauthController {

    @Autowired
    UserOauthService userOauthService;
    @Autowired
    UserService userService;
    @Autowired
    ParamService paramService;
    @Autowired
    TokenService tokenService;
    @Autowired
    WxApiService wxApiService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("uc:userOauth:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<UserOauthDTO> list = userOauthService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("uc:userOauth:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<UserOauthDTO> page = userOauthService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("uc:userOauth:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        UserOauthDTO data = userOauthService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<UserOauthDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("uc:userOauth:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody UserOauthDTO dto) {
        userOauthService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:userOauth:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody UserOauthDTO dto) {
        userOauthService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("uc:userOauth:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        userOauthService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("uc:userOauth:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        userOauthService.logicDeleteByIds(ids);

        return new Result<>();
    }

    /**
     * 微信小程序Oauth授权登录
     */
    @PostMapping("/oauthWxMaLoginByCodeAndUserInfo")
    @ApiOperation("Oauth授权登录")
    @LogLogin
    @AccessControl
    public Result<?> oauthWxMaLoginByCodeAndUserInfo(@Validated @RequestBody OauthWxMaLoginByCodeAndUserInfoRequest request) throws WxErrorException {
        // 获得登录配置
        JsonNode oauthCfg = paramService.getContentJsonNode(request.getParamCode());
        AssertUtils.isNull(oauthCfg, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 微信登录
        WxMaService wxService = wxApiService.getWxMaService(request.getParamCode());
        WxMaJscode2SessionResult jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());

        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(jscode2SessionResult.getSessionKey(), request.getRawData(), request.getSignature())) {
            return new Result<>().error(ErrorCode.WX_API_ERROR, "user check failed");
        }
        // 解密用户信息
        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(jscode2SessionResult.getSessionKey(), request.getEncryptedData(), request.getIv());

        // 更新或者插入Oauth表
        UserOauthEntity userOauth = userOauthService.saveOrUpdateByWxMaUserInfo(wxService.getWxMaConfig().getAppid(), userInfo);
        // 用户
        UserEntity user = null;
        if (userOauth.getUserId() != null) {
            user = userService.getById(userOauth.getUserId());
            if (null == user) {
                // 如果用户空了,同时结束所有绑定关系
                userOauthService.unbindByUserId(userOauth.getUserId());
            }
        }
        if (user == null) {
            // 根据业务提示错误或者自动创建用户
            return new Result<>().error(ErrorCode.OAUTH_NOT_BIND_ERROR);
        }
        // 登录成功
        Kv kv = Kv.init();
        kv.set(UcConst.TOKEN_HEADER, tokenService.createToken(user.getId(), loginChannelCfg));
        kv.set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<>().success(kv);
    }

    /**
     * Oauth授权登录
     */
    @PostMapping("/oauthWxMaLoginByCode")
    @ApiOperation("Oauth微信小程序授权登录")
    @LogLogin
    @AccessControl
    public Result<?> oauthWxMaLoginByCode(@Validated @RequestBody OauthLoginByCodeRequest request) throws WxErrorException {
        // 获得登录配置
        JsonNode oauthCfg = paramService.getContentJsonNode(request.getParamCode());
        AssertUtils.isNull(oauthCfg, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 微信登录(小程序)
        WxMaService wxService = wxApiService.getWxMaService(request.getParamCode());
        WxMaJscode2SessionResult jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());
        // 更新或者插入Oauth表
        UserOauthEntity userOauth = userOauthService.saveOrUpdateByWxMaJscode2SessionResult(wxService.getWxMaConfig().getAppid(), jscode2SessionResult);
        // 用户
        UserEntity user = null;
        if (userOauth.getUserId() != null) {
            user = userService.getById(userOauth.getUserId());
            if (null == user) {
                // 如果用户空了,同时结束所有绑定关系
                userOauthService.unbindByUserId(userOauth.getUserId());
            }
        }
        if (user == null) {
            // 根据业务提示错误或者自动创建用户
            return new Result<>().error(ErrorCode.OAUTH_NOT_BIND_ERROR);
        }
        // 登录成功
        Kv kv = Kv.init();
        kv.set(UcConst.TOKEN_HEADER, tokenService.createToken(user.getId(), loginChannelCfg));
        kv.set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<>().success(kv);
    }

}
