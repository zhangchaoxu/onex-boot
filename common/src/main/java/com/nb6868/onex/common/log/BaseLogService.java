package com.nb6868.onex.common.log;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BaseLogService {

    /**
     * 保存日志
     * 做分发，按日志类型和保存方式分别做保存
     *
     * @param log 日志内容
     */
    void saveLog(LogBody log);

    /**
     * 某段时间内的登录记录
     * @param user
     * @param tenantCode
     * @param minuteOffset
     * @param maxTimes
     * @return
     */
    List<LogBody> getListByUser(String user, String tenantCode, int minuteOffset, int maxTimes);

}
