package com.nb6868.onex.common.log;

import cn.hutool.core.lang.Dict;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LogBody implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 存储类型
     */
    private String storeType;
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
     * 耗时(毫秒)
     */
    private Long requestTime;
    /**
     * 状态  0：失败   1：成功
     */
    private Integer state;
    /**
     * 请求参数
     */
    private Dict requestParams;
    /**
     * 用户名
     */
    private String createName;
    /**
     * 租户编码
     */
    private String tenantCode;
}
