package com.nb6868.onex.sched.utils;

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
    private String id;

    /**
     * spring bean名称
     */
    private String name;
    /**
     * 参数
     */
    private JSONObject params;
    /**
     * cron表达式
     */
    private String cron;
    /**
     * 状态  0暂停/1正常
     */
    private Integer state;

}
