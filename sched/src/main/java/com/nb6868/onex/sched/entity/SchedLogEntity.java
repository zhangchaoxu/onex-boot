package com.nb6868.onex.sched.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.nb6868.onex.common.pojo.BaseTenantEntity;
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
@TableName(value = "sys_sched_log", autoResultMap = true)
@Alias("sys_sched_log")
public class SchedLogEntity extends BaseTenantEntity {

    /**
     * 任务ID
     */
	private Long schedId;
    /**
     * 任务名称
     */
	private String schedName;
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
}
