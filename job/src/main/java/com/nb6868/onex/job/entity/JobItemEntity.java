package com.nb6868.onex.job.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.jpa.Jackson2TypeHandler;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 系统-定时任务明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "sys_job_item", autoResultMap = true)
@Alias("sys_job_item")
public class JobItemEntity extends BaseEntity {

    /**
     * 任务ID
     */
	private Long jobId;
    /**
     * 状态
     */
	private Integer state;
    /**
     * 结果
     */
	private String result;
    /**
     * 耗时
     */
	private Integer timeInterval;
    /**
     * 租户编码
     */
	private String tenantCode;
    /**
     * 任务日志ID
     */
	private Long jobLogId;
    /**
     * 原始数据
     */
    @TableField(typeHandler = Jackson2TypeHandler.class)
	private JSONObject meta;
}
