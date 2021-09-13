package com.nb6868.onex.common.sched;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 任务信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TaskInfo implements Serializable {

    /**
     * ID
     */
    private String id;

    /**
     * spring bean名称
     */
    private String name;
    /**
     * 参数
     */
    private String params;
    /**
     * cron表达式
     */
    private String cron;
    /**
     * 任务状态  0：暂停  1：正常
     */
    private Integer state;

}
