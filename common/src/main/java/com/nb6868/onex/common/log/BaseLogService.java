package com.nb6868.onex.common.log;

import org.springframework.stereotype.Service;

@Service
public interface BaseLogService {

    /**
     * 保存到数据库
     * @param log 日志内容
     */
    void saveToDb(LogBody log);

}
