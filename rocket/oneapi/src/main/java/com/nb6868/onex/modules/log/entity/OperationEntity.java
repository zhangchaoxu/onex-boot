package com.nb6868.onex.modules.log.entity;

import com.nb6868.onex.booster.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("log_operation")
public class OperationEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户操作
     */
    private String operation;
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
     * 耗时(毫秒)
     */
    private Long requestTime;
    /**
     * 用户代理
     */
    private String userAgent;
    /**
     * 操作IP
     */
    private String ip;
    /**
     * 状态  0：失败   1：成功
     */
    private Integer status;
    /**
     * 用户名
     */
    private String createName;
}
