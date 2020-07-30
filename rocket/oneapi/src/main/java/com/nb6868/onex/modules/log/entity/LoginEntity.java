package com.nb6868.onex.modules.log.entity;

import com.nb6868.onex.booster.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录日志
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("log_login")
public class LoginEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 登录类型
     */
    private Integer type;
    /**
     * 登录结果
     */
    private Integer result;
    /**
     * 登录消息
     */
    private String msg;
    /**
     * 用户代理
     */
    private String userAgent;
    /**
     * 操作IP
     */
    private String ip;
    /**
     * 用户名
     */
    private String createName;

}
