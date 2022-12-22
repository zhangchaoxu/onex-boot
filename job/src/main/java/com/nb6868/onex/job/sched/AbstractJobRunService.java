package com.nb6868.onex.job.sched;

import cn.hutool.json.JSONObject;

/**
 * 任务执行服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public abstract class AbstractJobRunService {

    /**
     * 执行任务
     * @param runParams 执行参数
     * @param jobLogId 记录id
     */
    public abstract JobRunResult run(JSONObject runParams, Long jobLogId);

}
