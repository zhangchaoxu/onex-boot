package com.nb6868.onex.common.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 微信小程序通过code和用户信息登录请求
 * https://developers.weixin.qq.com/miniprogram/dev/api/open-api/login/wx.login.html
 * https://developers.weixin.qq.com/miniprogram/dev/api/open-api/user-info/wx.getUserInfo.html
 * https://developers.weixin.qq.com/miniprogram/dev/api/open-api/user-info/wx.getUserProfile.html
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "微信小程序通过code和用户信息登录请求")
public class OauthWxMaLoginByCodeAndUserInfoForm implements Serializable {

    @ApiModelProperty(value = "登录配置编码", example = "WX_MA")
    private String authConfigType = "WX_MA";

    @ApiModelProperty(value = "登录配置编码", example = "WX_MA")
    private String wechatMaConfigType = "WX_MA";

    @ApiModelProperty(value = "登录凭证wx.login获取", required = true)
    @NotBlank(message = "code不能为空")
    private String code;

    @ApiModelProperty(value = "用户信息rawData,wx.getUserInfo获取", required = true)
    @NotBlank(message = "rawData不能为空")
    private String rawData;

    @ApiModelProperty(value = "用户信息signature,wx.getUserInfo获取", required = true)
    @NotBlank(message = "signature不能为空")
    private String signature;

    @ApiModelProperty(value = "用户信息encryptedData,wx.getUserInfo获取", required = true)
    @NotBlank(message = "encryptedData不能为空")
    private String encryptedData;

    @ApiModelProperty(value = "用户信息iv,wx.getUserInfo获取", required = true)
    @NotBlank(message = "iv不能为空")
    private String iv;

}
