package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
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
     * 执行环境
     */
    private String env;
    /**
     * 日志记录类型
     */
    private String logType;
    /**
     * 参数
     */
    private String params;
    /**
     * cron表达式
     */
    private String cron;
    /**
     * 状态  0暂停/1正常
     */
    private Integer state;
    /**
     * 备注
     */
    private String remark;

}
