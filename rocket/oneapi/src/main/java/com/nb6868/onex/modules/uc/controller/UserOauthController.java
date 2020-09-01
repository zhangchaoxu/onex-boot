package com.nb6868.onex.modules.uc.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogLogin;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.modules.sys.service.ParamService;
import com.nb6868.onex.modules.uc.dto.OauthLoginByCodeRequest;
import com.nb6868.onex.modules.uc.dto.UserOauthDTO;
import com.nb6868.onex.modules.uc.entity.UserEntity;
import com.nb6868.onex.modules.uc.entity.UserOauthEntity;
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
     * Oauth授权登录
     */
    @PostMapping("/oauthLoginByCode")
    @ApiOperation("Oauth授权登录")
    @LogLogin
    public Result<?> oauthLoginByCode(@Validated @RequestBody OauthLoginByCodeRequest request) {
        // 获得登录配置
        JsonNode oauthCfg = paramService.getContentJsonNode(request.getParamCode());
        AssertUtils.isNull(oauthCfg, ErrorCode.UNKNOWN_LOGIN_TYPE);

        if (request.getType().startsWith("DINGTALK")) {
            // 钉钉登录
            /*DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/sns/getuserinfo_bycode");
            OapiSnsGetuserinfoBycodeRequest req = new OapiSnsGetuserinfoBycodeRequest();
            req.setTmpAuthCode(request.getCode());
            try {
                OapiSnsGetuserinfoBycodeResponse response = client.execute(req, oauthCfg.get("appid").asText(),oauthCfg.get("secret").asText());
                return new Result<>().success(response.getUserInfo());
            } catch (ApiException e) {
                e.printStackTrace();
                return new Result<>().error("钉钉接口调用失败");
            }*/
            return new Result<>().error("钉钉接口调用失败");
        } else if (request.getType().startsWith("WECHAT")) {
            // 微信登录(小程序)
            WxMaService wxService = wxApiService.getWxMaService(request.getParamCode());
            WxMaJscode2SessionResult jscode2SessionResult;
            try {
                jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());
            } catch (WxErrorException e) {
                return new Result<>().error("微信接口调用失败");
            }
            // 更新或者插入Oauth表
            UserOauthEntity userOauth = userOauthService.saveOrUpdateByWxMaJscode2SessionResult(wxService.getWxMaConfig().getAppid(), jscode2SessionResult);
            if (userOauth.getUserId() != null) {
                UserEntity userEntity = userService.getById(userOauth.getUserId());
                if (null == userEntity) {
                    // 如果用户空了,同时结束所有绑定关系
                    userOauthService.unbindByUserId(userOauth.getUserId());
                } else {
                    // todo 自动登录

                }
            }
            // todo 根据自己的业务判断比如是否有头像、是否关联用户等
            return new Result<>().success(userOauth);
        } else {
            return new Result<>().error("不支持的登录类型:" + request.getType());
        }
    }

    @PostMapping("/updateWxMaSession")
    @ApiOperation("更新微信")
    @AccessControl
    public Result<?> updateWxMaSession(@Validated @RequestBody OauthLoginByCodeRequest request) {
        // 微信登录(小程序)
        WxMaService wxService = wxApiService.getWxMaService(request.getParamCode());
        WxMaJscode2SessionResult jscode2SessionResult;
        try {
            jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());
        } catch (WxErrorException e) {
            return new Result<>().error("微信接口调用失败");
        }
        // 更新或者插入Oauth表
        userOauthService.saveOrUpdateByWxMaJscode2SessionResult(wxService.getWxMaConfig().getAppid(), jscode2SessionResult);
        return new Result<>();
    }

    @ApiOperation("更新微信小程序用户信息")
    @PostMapping("/updateWxMaUserInfo")
    @AccessControl
    public Result<?> updateWxMaUserInfo(@RequestParam String paramCode, @RequestParam String openid, @RequestParam String signature, @RequestParam String rawData, @RequestParam String encryptedData, String iv) {
        // 微信登录(小程序)
        WxMaService wxService = wxApiService.getWxMaService(paramCode);
        String sessionKey = userOauthService.getSessionKeyByOpenid(openid);
        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return new Result<>().error(ErrorCode.WX_API_ERROR, "user check failed");
        }
        // 解密用户信息
        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
        // 更新或者插入Oauth表
        userOauthService.saveOrUpdateByWxMaUserInfo(wxService.getWxMaConfig().getAppid(), userInfo);
        return new Result<>().success(userInfo);
    }

}
