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

    /*@ApiModelProperty(value = "登录配置项")
    private Map<String, Config> configs = new HashMap<>();

    @Data
    public static class Config {

        @ApiModelProperty(value = "登录类型")
        private String type;

        @JsonIgnore
        @ApiModelProperty(value = "token存储类型,db/cache/none")
        private String tokenStoreType = "db";

        *//**
         * 支持多端登录,表示可以在不同客户端登录,创建token的时候不判断原先的
         * 不支持多端登录,表示同一个帐号只能在一个地方登录,创建token的时候会将原先的token删除
         *//*
        @JsonIgnore
        @ApiModelProperty(value = "支持多客户端登录")
        private boolean multiLogin = true;

        @JsonIgnore
        @ApiModelProperty(value = "token密钥")
        private String tokenKey = "onex@2021";

        @JsonIgnore
        @ApiModelProperty(value = "token有效时间,默认7天,单位秒")
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

    }*/

}
