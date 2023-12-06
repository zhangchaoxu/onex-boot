package com.nb6868.onex.common.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 微信小程序通过code和用户手机号
 * https://developers.weixin.qq.com/miniprogram/dev/api/open-api/login/wx.login.html
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "微信小程序通过code和用户手机号登录请求")
public class OauthWxMaLoginByCodeAndPhoneForm implements Serializable {

    @Schema(description = "登录配置编码", example = "WX_MA")
    private String authConfigType = "WX_MA";

    @Schema(description = "登录配置编码", example = "WX_MA")
    private String wechatMaConfigType = "WX_MA";

    @Schema(description = "登录凭证wx.login获取", required = true)
    @NotBlank(message = "code不能为空")
    private String code;

    @Schema(description = "用户信息encryptedData,wx.getPhoneNumber获取", required = true)
    @NotBlank(message = "encryptedData不能为空")
    private String encryptedData;

    @Schema(description = "用户信息iv,wx.getPhoneNumber获取", required = true)
    @NotBlank(message = "iv不能为空")
    private String iv;

}
