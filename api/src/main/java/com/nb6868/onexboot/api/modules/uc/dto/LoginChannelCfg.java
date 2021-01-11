package com.nb6868.onexboot.api.modules.uc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 登录渠道配置信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "登录渠道配置信息")
public class LoginChannelCfg implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "登录类型")
    private Integer type;

    @JsonIgnore
    @ApiModelProperty(value = "首次登录强制修改密码")
    private Boolean forceUpdatePassword;

    @JsonIgnore
    @ApiModelProperty(value = "支持多客户端登录")
    private boolean multiLogin = true;

    @JsonIgnore
    @ApiModelProperty(value = "Token有效时间")
    private long expire = 604800L;

    @JsonIgnore
    @ApiModelProperty(value = "短信验证码有效毫秒长")
    private long smsCodeValidTime = 300000L;

    @JsonIgnore
    @ApiModelProperty(value = "自动延期")
    private boolean renewalToken = true;

    @JsonIgnore
    @ApiModelProperty(value = "基于角色控制")
    private boolean roleBase = true;

    @JsonIgnore
    @ApiModelProperty(value = "基于权限控制")
    private boolean permissionsBase = true;

    @ApiModelProperty(value = "验证码支持")
    private boolean captcha = false;

    @JsonIgnore
    @ApiModelProperty(value = "魔法验证码")
    private String magicCaptcha;

    @JsonIgnore
    @ApiModelProperty(value = "自动创建用户")
    private boolean autoCreate = false;

    @JsonIgnore
    @ApiModelProperty(value = "最多登录次数")
    private Integer tryTimesMax;

    @JsonIgnore
    @ApiModelProperty(value = "超过最大登录次数后锁定时间")
    private Long loginErrorLockTime;

    @JsonIgnore
    @ApiModelProperty(value = "自动创建用户的角色ids")
    private String autoCreateUserRoleIds;

    /**
     * 根据type获得默认配置
     * @param type
     * @return
     */
    public static LoginChannelCfg getDefaultCfg(int type) {
        // todo
        LoginChannelCfg loginCfg = new LoginChannelCfg();
        loginCfg.setType(type);
        return loginCfg;
    }

}
