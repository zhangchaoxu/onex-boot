package com.nb6868.onex.api.modules.uc.wx;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信扫码登录配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class WxScanProps {

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
