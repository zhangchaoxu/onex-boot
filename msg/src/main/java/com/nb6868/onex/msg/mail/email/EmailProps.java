package com.nb6868.onex.msg.mail.email;

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

    @ApiModelProperty(value = "发件人Host")
    private String host;

    @ApiModelProperty(value = "发件端口")
    private int port;

    @ApiModelProperty(value = "发件邮箱")
    private String username;

    @ApiModelProperty(value = "发件密码")
    private String password;

}
