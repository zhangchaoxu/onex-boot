package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
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
	 * 状态
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

}
