package com.nb6868.onex.job.sched;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 定时任务事件
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Getter
@Setter
public class JobEvent extends ApplicationEvent {

    private Long jobLogId;

    public JobEvent(Object source, Long jobLogId) {
        super(source);
        this.jobLogId = jobLogId;
    }
}
