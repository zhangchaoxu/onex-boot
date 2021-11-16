package com.nb6868.onex.common.dingtalk;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserGetByCodeResponse implements Serializable {

    @ApiModelProperty(value = "用户的userid")
    private String userid;

    @ApiModelProperty(value = "设备ID")
    private String device_id;

    @ApiModelProperty(value = "是否是管理员")
    private Boolean sys;

    @ApiModelProperty(value = "级别,1：主管理员/2：子管理员/100：老板/0：其他（如普通员工）")
    private Integer sys_level;

    @ApiModelProperty(value = "用户关联的unionId")
    private String associated_unionid;

    @ApiModelProperty(value = "用户unionId")
    private String unionid;

    @ApiModelProperty(value = "用户名字")
    private String name;

}
