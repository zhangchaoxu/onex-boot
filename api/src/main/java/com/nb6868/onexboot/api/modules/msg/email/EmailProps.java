package com.nb6868.onexboot.api.modules.msg.email;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 电子邮件配置信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@ApiModel(value = "电子邮件配置信息")
public class EmailProps implements Serializable {

    @ApiModelProperty(value = "发送平台")
    private String platform;

    @ApiModelProperty(value = "发件Host")
    private String senderHost;

    @ApiModelProperty(value = "发件端口")
    private int senderHostPort;

    @ApiModelProperty(value = "发件邮箱")
    private String senderUsername;

    @ApiModelProperty(value = "发件密码")
    private String senderPassword;

}
