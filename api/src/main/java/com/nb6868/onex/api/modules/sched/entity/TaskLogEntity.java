package com.nb6868.onex.api.modules.sched.entity;

import com.nb6868.onex.common.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sched_task_log")
public class TaskLogEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 任务id
	 */
	private Long taskId;
	/**
	 * 任务名称
	 */
	private String taskName;
	/**
	 * 参数
	 */
	private String params;
	/**
	 * 任务状态    0：失败    1：成功
	 */
	private Integer state;
	/**
	 * 失败信息
	 */
	private String error;
	/**
	 * 耗时(单位：毫秒)
	 */
	private Integer times;

}
