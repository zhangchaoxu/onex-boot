package com.nb6868.onex.modules.uc.wx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信配置信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@ApiModel(value = "微信配置信息")
public class WxProp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * APP("移动应用"),
     * WEB("网站应用"),
     * MP("公众帐号"),
     * MA("小程序"),
     * THIRD_PRAT("第三方应用");
     */
    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "appId")
    private String appid;

    @ApiModelProperty(value = "secret")
    private String secret;

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "EncodingAESKey")
    private String aesKey;

    @ApiModelProperty(value = "模板消息id")
    private String templateId;

    @ApiModelProperty(value = "消息格式，XML或者JSON")
    private String msgDataFormat;

}
