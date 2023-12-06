package com.nb6868.onex.common.auth;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "token key")
    private String tokenHeaderKey = "auth-token";

    @Schema(description = "token类型在jwt中的key")
    private String tokenJwtKey = "sub";

    @Schema(description = "访问白名单")
    private String whiteList;

    @Schema(description = "访问控制")
    private AccessControl accessControl;

    @Data
    public static class AccessControl {

        @Schema(description = "是否启用")
        private boolean enable = true;

        @Schema(description = "扫描路径")
        private String scanPackage;
    }

}
