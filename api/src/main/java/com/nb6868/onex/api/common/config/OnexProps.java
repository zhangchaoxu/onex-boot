package com.nb6868.onex.api.common.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nb6868.onex.api.modules.uc.dingtalk.DingtalkScanProps;
import com.nb6868.onex.api.modules.uc.wx.WxScanProps;
import com.nb6868.onex.common.config.YamlPropertySourceFactory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * OneX相关配置文件
 *
 * @author Charles
 */
@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "onex")
@PropertySource(value = "classpath:onex.yml", factory = YamlPropertySourceFactory.class)
public class OnexProps implements Serializable {

    /**
     * 管理后台登录配置
     */
    private final LoginAdminProps loginAdminProps = new LoginAdminProps();
    /**
     * 前端应用登录配置
     */
    private final LoginAppProps loginAppProps = new LoginAppProps();

    @Data
    @NoArgsConstructor
    public static class LoginAppProps implements Serializable {

        @JsonIgnore
        @ApiModelProperty(value = "登录配置源")
        private LoginPropsSource source = LoginPropsSource.PROPS;

    }

    @Data
    @NoArgsConstructor
    public static class LoginAdminProps implements Serializable {

        @JsonIgnore
        @ApiModelProperty(value = "登录配置源")
        private LoginPropsSource source = LoginPropsSource.PROPS;

        @ApiModelProperty(value = "开放注册")
        private boolean register;

        @ApiModelProperty(value = "开放修改密码")
        private boolean forgetPassword;

        @ApiModelProperty(value = "开放帐号登录")
        private boolean usernamePasswordLogin;

        @ApiModelProperty(value = "帐号登录配置")
        private LoginProps usernamePasswordLoginProps;

        @ApiModelProperty(value = "开放手机号验证码登录")
        private boolean mobileSmscodeLogin;

        @ApiModelProperty(value = "手机号验证码登录配置")
        private LoginProps mobileSmscodeLoginProps;

        @ApiModelProperty(value = "开放微信扫码登录")
        private boolean wechatScanLogin;

        @ApiModelProperty(value = "微信扫码登录配置")
        private LoginProps wechatScanLoginProps;

        @ApiModelProperty(value = "微信扫码配置内容")
        private WxScanProps wechatScanProps;

        @ApiModelProperty(value = "开放钉钉扫码登录")
        private boolean dingtalkScanLogin;

        @ApiModelProperty(value = "钉钉扫码登录配置")
        private LoginProps dingtalkScanLoginProps;

        @ApiModelProperty(value = "钉钉扫码配置内容")
        private DingtalkScanProps dingtalkScanProps;

    }

}
