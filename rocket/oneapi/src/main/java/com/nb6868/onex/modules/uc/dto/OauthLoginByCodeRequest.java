package com.nb6868.onex.modules.uc.dto;

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
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "通过code第三方登录请求")
public class OauthLoginByCodeRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "登录类型", required = true)
    @NotBlank(message = "登录类型")
    private String type;

    @ApiModelProperty(value = "配置信息编码", required = true)
    @NotBlank(message = "配置信息编码")
    private String paramCode;

    @ApiModelProperty(value = "用户登录凭证", required = true)
    @NotBlank(message = "code")
    private String code;

}
