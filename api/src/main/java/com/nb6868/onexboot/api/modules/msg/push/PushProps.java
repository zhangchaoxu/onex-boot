package com.nb6868.onexboot.api.modules.msg.push;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 消息推送配置信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@ApiModel(value = "消息推送配置信息")
public class PushProps implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "platform")
    private String platform;

    @ApiModelProperty(value = "masterSecret")
    private String masterSecret;

    @ApiModelProperty(value = "appKey")
    private String appKey;

}
