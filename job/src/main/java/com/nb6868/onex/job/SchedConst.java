package com.nb6868.onex.sys;

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
    enum SchedState {
        /**
         * 暂停
         */
        PAUSE(0),
        /**
         * 正常
         */
        NORMAL(1);

        private int value;

        SchedState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 任务日志状态
     */
    enum SchedLogState {
        /**
         * 初始化
         */
        INIT(0),
        /**
         * 开始运行
         */
        START(1),
        /**
         * 运行中
         */
        RUNNING(10),
        /**
         * 任务完成
         */
        COMPLETED(100),
        /**
         * 取消
         */
        NORMAL(-1),
        /**
         * 出现错误
         */
        ERROR(-100);

        private int value;

        SchedLogState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
