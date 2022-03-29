package com.nb6868.onex.uc.utils;

import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 任务信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TaskInfo implements Serializable {

    /**
     * ID
     */
    private Long id;
    /**
     * spring bean名称
     */
    private String name;
    /**
     * 指定执行环境
     */
    private String env;
    /**
     * 参数
     */
    private JSONObject params;
    /**
     * cron表达式
     */
    private String cron;
    /**
     * 日志记录类型
     */
    private String logType;
    /**
     * 状态  0暂停/1正常
     */
    private Integer state;

}
