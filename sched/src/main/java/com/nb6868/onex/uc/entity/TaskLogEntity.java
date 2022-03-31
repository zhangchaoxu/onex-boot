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
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "sched_task_log", autoResultMap = true)
@Alias("sched_task_log")
public class TaskLogEntity extends BaseEntity {

    /**
     * 任务ID
     */
	private Long taskId;
    /**
     * 任务名称
     */
	private String taskName;
    /**
     * 参数
     */
	@TableField(typeHandler = JacksonTypeHandler.class)
	private JSONObject params;
    /**
     * 日志状态
     */
	private Integer state;
    /**
     * 结果
     */
	private String result;
    /**
     * 错误信息
     */
	private String error;
    /**
     * 耗时(单位：毫秒)
     */
	private Long times;
    /**
     * 租户编码
     */
	private String tenantCode;
}
