package com.nb6868.onex.common.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 通过code第三方登录请求,支持钉钉和微信
 * https://developers.weixin.qq.com/miniprogram/dev/api/open-api/user-info/wx.getUserInfo.html
 * https://ding-doc.dingtalk.com/doc#/serverapi2/etaarr/278e10e6
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "通过code第三方登录请求")
public class OauthLoginByCodeForm implements Serializable {

     @Schema(description = "配置信息编码", required = true)
    @NotEmpty(message = "配置信息编码")
    private String type = "WX_MA";

     @Schema(description = "用户登录凭证", required = true)
    @NotEmpty(message = "code")
    private String code;

}
