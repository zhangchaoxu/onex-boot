package com.nb6868.onex.modules.log.entity;

import com.nb6868.onex.booster.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 异常日志
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("log_error")
public class ErrorEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 请求URI
     */
    private String uri;
    /**
     * 请求方式
     */
    private String method;
    /**
     * 请求参数
     */
    private String params;
    /**
     * 用户代理
     */
    private String userAgent;
    /**
     * 操作IP
     */
    private String ip;
    /**
     * 异常信息
     */
    private String content;

}
