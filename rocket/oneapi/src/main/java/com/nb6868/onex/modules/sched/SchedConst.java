package com.nb6868.onex.modules.sched;

/**
 * 定时任务相关常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface SchedConst {

    /**
     * 任务前缀
     */
    String JOB_NAME = "TASK_";
    /**
     * 任务调度参数key
     */
    String JOB_PARAM_KEY = "JOB_PARAM_KEY";

    /**
     * 定时任务状态
     */
    enum TaskStatus {
        /**
         * 暂停
         */
        PAUSE(0),
        /**
         * 正常
         */
        NORMAL(1);

        private int value;

        TaskStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
