package com.nb6868.onex.common.auth;

import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.TenantGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Code登录请求")
public class CodeLoginForm implements Serializable {

    @ApiModelProperty(value = "登录配置编码", example = "ADMIN_USERNAME_PASSWORD")
    @NotEmpty(message = "登录类型不能为空", groups = {DefaultGroup.class})
    private String type;

    @ApiModelProperty(value = "登录编码")
    @NotEmpty(message = "登录编码不能为空", groups = {DefaultGroup.class})
    private String code;

    @ApiModelProperty(value = "租户编码", required = true)
    @NotEmpty(message = "租户编码不能为空", groups = {TenantGroup.class})
    private String tenantCode;

}
