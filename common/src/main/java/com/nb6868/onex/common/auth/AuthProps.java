package com.nb6868.onex.common.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 登录配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@Component
@ConfigurationProperties(prefix = "onex.auth")
public class AuthProps {

    @ApiModelProperty(value = "token key")
    private String tokenHeaderKey = "auth-token";

    @ApiModelProperty(value = "token类型在jwt中的key")
    private String tokenJwtKey = "sub";

    @ApiModelProperty(value = "访问白名单")
    private String whiteList;

    @ApiModelProperty(value = "访问控制")
    private AccessControl accessControl;

    @Data
    public static class AccessControl {

        @ApiModelProperty(value = "是否启用")
        private boolean enable = true;

        @ApiModelProperty(value = "扫描路径")
        private String scanPackage;

    }

}
