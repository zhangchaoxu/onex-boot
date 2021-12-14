package com.nb6868.onex.common.log;

import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LogBody extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    private String type;
    /**
     * 内容
     */
    private String content;
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
    private Integer state;
    /**
     * 用户名
     */
    private String createName;

}
