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
    /*{
        LogEntity logEntity = ConvertUtils.sourceToTarget(log, LogEntity.class);
        save(logEntity);
    }*/


    /**
     * 获得连续登录失败的次数
     */
    int getContinuousLoginErrorTimes(String user, String tenantCode, int minuteOffset, int limit);
    /*{
        // 先找到最近minuteOffset分钟内的limit登录记录
        List<LogEntity> list = query()
                .select("state")
                .and(queryWrapper -> queryWrapper.eq("type", "login").or().eq("type", "loginEncrypt"))
                .eq("create_name", user)
                .eq(StrUtil.isNotBlank(user), "tenant_code", tenantCode)
                .ge("create_time", DateUtil.offsetMinute(new Date(), -minuteOffset))
                .orderByDesc("create_time")
                .last(StrUtil.format(Const.LIMIT_FMT, limit))
                .list();
        // 错误次数
        int errorCount = 0;
        for (LogEntity log : list) {
            if (log.getState() != Const.ResultEnum.SUCCESS.value()) {
                errorCount++;
            } else {
                break;
            }
        }
        return errorCount;
    }*/

}
