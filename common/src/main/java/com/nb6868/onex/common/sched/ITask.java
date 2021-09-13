package com.nb6868.onex.common.sched;

import cn.hutool.json.JSONObject;

/**
 * 定时任务接口，所有定时任务都要实现该接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ITask {

    /**
     * 执行定时任务接口
     *
     * @param params   参数，多参数使用JSON数据
     */
    JSONObject run(JSONObject params);

}
