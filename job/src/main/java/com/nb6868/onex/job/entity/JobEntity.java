package com.nb6868.onex.job.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.nb6868.onex.common.pojo.BaseTenantEntity;
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
@TableName(value = "sys_job", autoResultMap = true)
@Alias("sys_job")
public class JobEntity extends BaseTenantEntity {

    /**
     * 名称
     */
    private String code;
    /**
     * 状态
     */
    private Integer state;
    /**
     * cron表达式
     */
    private String cron;
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
}
