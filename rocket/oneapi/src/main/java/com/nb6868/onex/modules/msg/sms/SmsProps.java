package com.nb6868.onex.modules.msg.sms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 短信配置信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@ApiModel(value = "短信配置信息")
public class SmsProps implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "发送平台")
    private String platform;

    @ApiModelProperty(value = "appId")
    private String appId;

    @ApiModelProperty(value = "appKey")
    private String appKey;

    @ApiModelProperty(value = "appSecret")
    private String appSecret;

    @ApiModelProperty(value = "sign")
    private String sign;

    @ApiModelProperty(value = "tplId")
    private String tplId;

    @ApiModelProperty(value = "regionId")
    private String regionId;

}
