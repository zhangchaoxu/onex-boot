package com.nb6868.onex.uc;

import com.nb6868.onex.common.log.BaseLogService;
import com.nb6868.onex.common.log.LogBody;
import org.springframework.stereotype.Service;

@Service
public class LogService implements BaseLogService {

    @Override
    public void saveLog(LogBody log) {

    }

    @Override
    public int getContinuousLoginErrorTimes(String user, String tenantCode, int minuteOffset, int limit) {
        return 0;
    }
}
