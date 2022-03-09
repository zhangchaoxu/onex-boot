package com.nb6868.onex.common.log;

import org.springframework.stereotype.Service;

@Service
public interface BaseLogService {

    /**
     * 保存日志
     * 做分发，按日志类型和保存方式分别做保存
     *
     * @param log 日志内容
     */
    void saveLog(LogBody log);

}
