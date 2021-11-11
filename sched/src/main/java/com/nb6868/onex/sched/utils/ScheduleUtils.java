package com.nb6868.onex.sched.utils;

import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.sched.SchedConst;
import org.quartz.*;

/**
 * 定时任务工具类
 * 修改qrtz_job_details和qrtz_triggers表内容
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class ScheduleUtils {

    /**
     * 获取触发器key
     */
    public static TriggerKey getTriggerKey(Long jobId) {
        return TriggerKey.triggerKey(SchedConst.JOB_NAME + jobId);
    }

    /**
     * 获取jobKey
     */
    public static JobKey getJobKey(Long jobId) {
        return JobKey.jobKey(SchedConst.JOB_NAME + jobId);
    }

    /**
     * 获取表达式触发器
     */
    public static CronTrigger getCronTrigger(Scheduler scheduler, Long jobId) {
        try {
            return (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId));
        } catch (SchedulerException e) {
            throw new OnexException(ErrorCode.JOB_ERROR, e);
        }
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Class<? extends Job> jobClass, Scheduler scheduler, TaskInfo taskInfo) {
        try {
            // 启动调度器
            scheduler.start();
            // 构建job信息
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(taskInfo.getId())).build();
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(taskInfo.getCron()).withMisfireHandlingInstructionDoNothing();
            // 按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(taskInfo.getId())).withSchedule(scheduleBuilder).build();
            // 放入参数，运行时的方法可以获取
            jobDetail.getJobDataMap().put(SchedConst.JOB_PARAM_KEY, taskInfo);
            scheduler.scheduleJob(jobDetail, trigger);

            // 暂停任务
            if (taskInfo.getState() == SchedConst.TaskState.PAUSE.getValue()) {
                pauseJob(scheduler, taskInfo.getId());
            }
        } catch (SchedulerException e) {
            throw new OnexException(ErrorCode.JOB_ERROR, e);
        }
    }

    /**
     * 更新定时任务
     */
    public static void updateScheduleJob(Scheduler scheduler, TaskInfo taskInfo) {
        try {
            // 启动调度器
            scheduler.start();
            TriggerKey triggerKey = getTriggerKey(taskInfo.getId());
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(taskInfo.getCron()).withMisfireHandlingInstructionDoNothing();
            CronTrigger trigger = getCronTrigger(scheduler, taskInfo.getId());
            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            // 参数
            trigger.getJobDataMap().put(SchedConst.JOB_PARAM_KEY, taskInfo);
            scheduler.rescheduleJob(triggerKey, trigger);
            // 暂停任务
            if (taskInfo.getState() == SchedConst.TaskState.PAUSE.getValue()) {
                pauseJob(scheduler, taskInfo.getId());
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new OnexException(ErrorCode.JOB_ERROR, e);
        }
    }

    /**
     * 立即执行任务
     */
    public static void run(Scheduler scheduler, TaskInfo taskInfo) {
        try {
            // 参数
            JobDataMap dataMap = new JobDataMap();
            dataMap.put(SchedConst.JOB_PARAM_KEY, taskInfo);
            scheduler.triggerJob(getJobKey(taskInfo.getId()), dataMap);
        } catch (SchedulerException e) {
            throw new OnexException(ErrorCode.JOB_ERROR, e);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 暂停任务
     */
    public static void pauseJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.pauseJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new OnexException(ErrorCode.JOB_ERROR, e);
        }
    }

    /**
     * 恢复任务
     */
    public static void resumeJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.resumeJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new OnexException(ErrorCode.JOB_ERROR, e);
        }
    }

    /**
     * 删除定时任务
     */
    public static void deleteScheduleJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.deleteJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new OnexException(ErrorCode.JOB_ERROR, e);
        }
    }

}
