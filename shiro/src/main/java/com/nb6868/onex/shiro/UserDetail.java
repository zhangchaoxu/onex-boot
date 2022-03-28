package com.nb6868.onex.shiro;

import com.nb6868.onex.common.auth.AuthProps;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class UserDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private Integer type;
    private String tenantCode;
    /**
     * 登录配置
     */
    private AuthProps.Config loginConfig;

}
