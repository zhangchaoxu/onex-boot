package com.nb6868.onex.common.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nb6868.onex.common.config.YamlPropertySourceFactory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 登录配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@Component
@ConfigurationProperties(prefix = "login")
@PropertySource(value = "classpath:onex.yml", factory = YamlPropertySourceFactory.class)
public class LoginProps {

    @ApiModelProperty(value = "登录设置")
    private Map<String, Settings> settings;

    @Data
    public static class Settings {

        @ApiModelProperty(value = "注册")
        private boolean register;

        @ApiModelProperty(value = "修改密码")
        private boolean forgetPassword;

        @ApiModelProperty(value = "登录方式")
        private List<String> types;

    }

    @ApiModelProperty(value = "登录配置项")
    private Map<String, Config> configs;

    @Data
    public static class Config {

        @ApiModelProperty(value = "登录类型")
        private String type;

        /**
         * 支持多端登录,表示可以在不同客户端登录,创建token的时候不判断原先的
         * 不支持多端登录,表示同一个帐号只能在一个地方登录,创建token的时候会将原先的token删除
         */
        @JsonIgnore
        @ApiModelProperty(value = "支持多客户端登录")
        private boolean multiLogin = true;

        @JsonIgnore
        @ApiModelProperty(value = "token策略")
        private String tokenPolicy = "uuid";

        @JsonIgnore
        @ApiModelProperty(value = "token有效时间")
        private Integer tokenExpire = 604800;

        @JsonIgnore
        @ApiModelProperty(value = "token是否自动延期")
        private boolean tokenRenewal = true;

        @JsonIgnore
        @ApiModelProperty(value = "基于角色控制")
        private boolean roleBase = false;

        @JsonIgnore
        @ApiModelProperty(value = "基于权限控制")
        private boolean permissionBase = true;

        @ApiModelProperty(value = "是否需要验证码")
        private boolean captcha = false;

        @JsonIgnore
        @ApiModelProperty(value = "魔术验证码")
        private String magicCaptcha;

        @JsonIgnore
        @ApiModelProperty(value = "最多登录次数")
        private Integer tryTimesMax;

        @JsonIgnore
        @ApiModelProperty(value = "超过最大登录次数后锁定时间")
        private Long loginErrorLockTime;

        @JsonIgnore
        @ApiModelProperty(value = "自动创建用户")
        private boolean autoCreate = false;

        @JsonIgnore
        @ApiModelProperty(value = "自动创建用户的角色ids")
        private String autoCreateUserRoleIds;

        @JsonIgnore
        @ApiModelProperty(value = "首次登录强制修改密码")
        private Boolean forceUpdatePassword;
    }

}
