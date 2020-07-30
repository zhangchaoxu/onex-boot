package com.nb6868.onex.modules.wx.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 微信公众号登录请求
 * https://developers.weixin.qq.com/miniprogram/dev/api/open-api/user-info/wx.getUserInfo.html
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "微信公众号登录请求")
public class WxMpLoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "配置信息编码", required = true)
    @NotBlank(message = "配置信息编码")
    private String paramCode;

    @ApiModelProperty(value = "用户登录凭证", required = true)
    @NotBlank(message = "code")
    private String code;

}
