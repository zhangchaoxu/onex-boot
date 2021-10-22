package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "用户名密码登录请求")
public class UsernamePasswordLoginRequest {

    @ApiModelProperty(value = "用户名", required = true)
    @NotEmpty(message = "用户名不能为空", groups = {DefaultGroup.class})
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    @NotEmpty(message = "密码不能为空", groups = {DefaultGroup.class})
    private String password;

}
