package com.nb6868.onex.common.log;

import org.springframework.stereotype.Service;

/**
 * 基础日志服务,实现方法见sys中的LogService
 */
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
     * 获得连续登录失败的次数
     */
    int getContinuousLoginErrorTimes(String user, String tenantCode, int minuteOffset, int limit);

}
