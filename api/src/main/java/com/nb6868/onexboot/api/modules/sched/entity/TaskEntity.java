package com.nb6868.onexboot.api.modules.sched.entity;

import com.nb6868.onexboot.common.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sched_task")
public class TaskEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

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
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}
