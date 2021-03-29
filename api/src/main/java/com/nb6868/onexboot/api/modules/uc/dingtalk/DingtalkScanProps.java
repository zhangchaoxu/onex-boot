package com.nb6868.onexboot.api.modules.uc.dingtalk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 钉钉扫码登录配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class DingtalkScanProps {

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
