package com.nb6868.onex.sys.entity;

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
@TableName(value = "sys_sched", autoResultMap = true)
@Alias("sys_sched")
public class SchedEntity extends BaseTenantEntity {

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
}