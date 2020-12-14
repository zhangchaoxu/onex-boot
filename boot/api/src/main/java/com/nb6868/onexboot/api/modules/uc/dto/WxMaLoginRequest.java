package com.nb6868.onexboot.api.modules.uc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 微信小程序登录请求
 * https://developers.weixin.qq.com/miniprogram/dev/api/open-api/user-info/wx.getUserInfo.html
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "微信小程序登录请求")
public class WxMaLoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "配置信息编码", required = true)
    @NotBlank(message = "配置信息编码")
    private String paramCode;

    // 由https://developers.weixin.qq.com/miniprogram/dev/api/open-api/login/wx.login.html获取
    @ApiModelProperty(value = "用户登录凭证（有效期五分钟）", required = true)
    @NotBlank(message = "code")
    private String code;

    // 以下由https://developers.weixin.qq.com/miniprogram/dev/api/open-api/user-info/wx.getUserInfo.html获得
    @ApiModelProperty(value = "不包括敏感信息的原始数据字符串，用于计算签名", required = true)
    @NotBlank(message = "rawData")
    private String rawData;

    @ApiModelProperty(value = "使用 sha1( rawData + sessionkey ) 得到字符串，用于校验用户信息", required = true)
    @NotBlank(message = "signature")
    private String signature;

    @ApiModelProperty(value = "包括敏感数据在内的完整用户信息的加密数据", required = true)
    @NotBlank(message = "encryptedData")
    private String encryptedData;

    @ApiModelProperty(value = "加密算法的初始向量", required = true)
    @NotBlank(message = "iv")
    private String iv;

}
