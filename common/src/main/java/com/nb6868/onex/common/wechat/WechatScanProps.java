package com.nb6868.onex.common.wechat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信扫码登录配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class WechatScanProps {

    @JsonIgnore
    @ApiModelProperty(value = "secret")
    private String secret;

    @JsonIgnore
    @ApiModelProperty(value = "cropId")
    private String cropId;

    @ApiModelProperty(value = "appid")
    private String appid;

    @ApiModelProperty(value = "callback")
    private String callback;

}
