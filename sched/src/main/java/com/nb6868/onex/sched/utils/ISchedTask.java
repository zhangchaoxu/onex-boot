package com.nb6868.onex.sched.utils;

/**
 * 定时任务接口，所有定时任务都要实现该接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ISchedTask {

    /**
     * 执行定时任务接口
     *
     * @param taskInfo 任务
     */
    ScheduleRunResult run(SchedTask taskInfo, Long taskLogId);

}
