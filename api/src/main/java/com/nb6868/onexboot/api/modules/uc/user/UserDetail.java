package com.nb6868.onexboot.api.modules.uc.user;

import com.nb6868.onexboot.api.common.config.LoginProps;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
    private String realName;
    private String headUrl;
    private Integer gender;
    private String email;
    private String mobile;
    private Long deptId;
    private Long tenantId;
    private String tenantName;
    private String password;
    private Integer state;
    private Integer type;
    /**
     * 部门数据权限
     */
    private List<Long> deptIdList;
    /**
     * 登录配置
     */
    private LoginProps loginProps;

    /**
     * 是否匿名用户
     */
    public boolean isAnon() {
        return getId() != null && -1L == getId();
    }

}
