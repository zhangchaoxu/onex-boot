package com.nb6868.onex.modules.uc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 登录配置信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "登录配置信息")
public class LoginCfg implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "开放注册")
    private boolean register;

    @ApiModelProperty(value = "开放忘记密码")
    private boolean forgetPassword;

    @ApiModelProperty(value = "各个渠道配置")
    private List<LoginChannel> channels;

}
