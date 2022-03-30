package com.nb6868.onex.uc.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "sched_task", autoResultMap = true)
@Alias("sched_task")
public class TaskEntity extends BaseEntity {

    /**
     * 名称
     */
    private String name;
    /**
     * 状态
     */
    private Integer state;
    /**
     * cron表达式
     */
    private String cron;
    /**
     * 允许执行环境
     */
    private String env;
    /**
     * 日志类型
     */
    private String logType;
    /**
     * 参数
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject params;
    /**
     * 备注
     */
    private String remark;
    /**
     * 租户编码
     */
    private String tenantCode;
}
