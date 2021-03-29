package com.nb6868.onexboot.api.common.config;

import com.nb6868.onexboot.common.config.YamlPropertySourceFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

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
     * 登录配置源,支持配置文件和数据库
     */
    private LoginPropsSource loginPropsSource = LoginPropsSource.PROPS;

    /**
     * 登录配置
     */
    private final LoginProps loginProps = new LoginProps();

    @Data
    @NoArgsConstructor
    public static class LoginProps implements Serializable {

        /**
         * 登录类型
         */
        private String type;
        /**
         * 是否支持多端登录
         * 支持多端登录,表示可以在不同客户端登录,创建token的时候不判断原先的
         * 不支持多端登录,表示同一个帐号只能在一个地方登录,创建token的时候会将原先的token删除
         */
        @JsonIgnore
        @ApiModelProperty(value = "支持多客户端登录")
        private boolean multiLogin = false;
        /**
         * token策略,jwt或者uuid
         */
        @JsonIgnore
        @ApiModelProperty(value = "支持多客户端登录")
        private TokenPolicy tokenPolicy = TokenPolicy.UUID;
        /**
         * token有效时间
         */
        @JsonIgnore
        @ApiModelProperty(value = "支持多客户端登录")
        private Long tokenExpire = 604800L;
        /**
         * token是否自动延期
         */
        @JsonIgnore
        @ApiModelProperty(value = "支持多客户端登录")
        private boolean tokenRenewal = true;
        /**
         * 基于角色控制
         */
        private boolean roleBase = false;
        /**
         * 基于权限控制
         */
        private boolean permissionBase = true;
        /**
         * 是否有验证码
         */
        @ApiModelProperty(value = "是否需要验证码")
        private boolean captcha = false;
        /**
         * 魔术验证码
         */
        @JsonIgnore
        @ApiModelProperty(value = "魔术验证码")
        private String magicCaptcha = "";

    }
}
