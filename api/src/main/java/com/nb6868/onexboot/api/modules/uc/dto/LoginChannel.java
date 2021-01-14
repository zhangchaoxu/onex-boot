package com.nb6868.onexboot.api.modules.uc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 登录渠道
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "登录渠道")
public class LoginChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "显示名称")
    private String title;

    @ApiModelProperty(value = "开放")
    private Boolean enable;

    @ApiModelProperty(value = "渠道配置")
    private LoginTypeConfig cfg;

}
