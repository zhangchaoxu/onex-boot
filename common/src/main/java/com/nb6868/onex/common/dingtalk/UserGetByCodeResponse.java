package com.nb6868.onex.common.dingtalk;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserGetByCodeResponse implements Serializable {

     @Schema(description = "用户的userid")
    private String userid;

     @Schema(description = "设备ID")
    private String device_id;

     @Schema(description = "是否是管理员")
    private Boolean sys;

     @Schema(description = "级别,1：主管理员/2：子管理员/100：老板/0：其他（如普通员工）")
    private Integer sys_level;

     @Schema(description = "用户关联的unionId")
    private String associated_unionid;

     @Schema(description = "用户unionId")
    private String unionid;

     @Schema(description = "用户名字")
    private String name;

}
