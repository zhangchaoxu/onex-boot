package com.nb6868.onex.common.dingtalk;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserContactResponse implements Serializable {

    @ApiModelProperty(value = "nick")
    private String nick;

    @ApiModelProperty(value = "avatarUrl")
    private String avatarUrl;

    @ApiModelProperty(value = "mobile")
    private String mobile;

    @ApiModelProperty(value = "openId")
    private String openId;

    @ApiModelProperty(value = "unionId")
    private String unionId;

    @ApiModelProperty(value = "email")
    private String email;

    @ApiModelProperty(value = "stateCode")
    private String stateCode;

}
