package com.nb6868.onex.common.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
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
@ApiModel(value = "通过code第三方登录请求")
public class OauthLoginByCodeForm implements Serializable {

    @ApiModelProperty(value = "配置信息编码", required = true)
    @NotBlank(message = "配置信息编码")
    private String type = "WX_MA";

    @ApiModelProperty(value = "用户登录凭证", required = true)
    @NotBlank(message = "code")
    private String code;

}
