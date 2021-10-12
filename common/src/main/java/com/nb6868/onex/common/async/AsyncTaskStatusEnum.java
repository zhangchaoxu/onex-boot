package com.nb6868.onex.common.async;

/**
 * 任务状态枚举
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public enum AsyncTaskStatusEnum {

    STARTED(1, "任务已经启动"),
    RUNNING(0, "任务正在运行"),
    SUCCESS(100, "任务执行成功"),
    FAILED(-100, "任务执行失败");

    private int state;
    private String stateInfo;

    AsyncTaskStatusEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

}
