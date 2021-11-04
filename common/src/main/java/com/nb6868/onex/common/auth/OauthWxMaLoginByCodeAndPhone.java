package com.nb6868.onex.common.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 微信小程序通过code和用户手机号
 * https://developers.weixin.qq.com/miniprogram/dev/api/open-api/login/wx.login.html
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "微信小程序通过code和用户手机号登录请求")
public class OauthWxMaLoginByCodeAndPhone implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "登录配置编码", example = "WX_MA")
    private String authConfigType = "WX_MA";

    @ApiModelProperty(value = "登录配置编码", example = "WX_MA")
    private String wechatMaConfigType = "WX_MA";

    @ApiModelProperty(value = "登录凭证wx.login获取", required = true)
    @NotBlank(message = "code不能为空")
    private String code;

    @ApiModelProperty(value = "用户信息encryptedData,wx.getPhoneNumber获取", required = true)
    @NotBlank(message = "encryptedData不能为空")
    private String encryptedData;

    @ApiModelProperty(value = "用户信息iv,wx.getPhoneNumber获取", required = true)
    @NotBlank(message = "iv不能为空")
    private String iv;

}
