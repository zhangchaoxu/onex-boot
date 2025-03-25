package com.nb6868.onex.sys.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.log.BaseLogService;
import com.nb6868.onex.common.log.LogBody;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.sys.SysConst;
import com.nb6868.onex.sys.dao.LogDao;
import com.nb6868.onex.sys.dto.LogDTO;
import com.nb6868.onex.sys.entity.LogEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class LogService extends DtoService<LogDao, LogEntity, LogDTO> implements BaseLogService {

    @Override
    public void saveLog(LogBody log) {
        LogEntity logEntity = ConvertUtils.sourceToTarget(log, LogEntity.class);
        save(logEntity);
    }

    @Override
    public int getContinuousLoginErrorTimes(String user, String tenantCode, int minuteOffset) {
        // 错误次数
        AtomicInteger errorCount = new AtomicInteger(0);
        // 先找到最近minuteOffset分钟内的limit登录记录
        List<LogEntity> list = lambdaQuery()
                .select(LogEntity::getState)
                //.and(queryWrapper -> queryWrapper.eq("type", "login").or().eq("type", "loginEncrypt"))
                .eq(LogEntity::getType, SysConst.LogTypeEnum.LOGIN.getCode())
                .eq(LogEntity::getCreateName, user)
                .eq(StrUtil.isNotBlank(tenantCode), LogEntity::getTenantCode, tenantCode)
                .ge(LogEntity::getCreateTime, DateUtil.offsetMinute(new Date(), -minuteOffset))
                .orderByDesc(LogEntity::getCreateTime)
                //.last(StrUtil.format(Const.LIMIT_FMT, limit))
                .list();
        for (LogEntity log : list) {
            if (ObjUtil.equal(log.getState(), ErrorCode.ACCOUNT_PASSWORD_ERROR)) {
                errorCount.incrementAndGet();
            } else if (ObjUtil.equal(log.getState(), ErrorCode.SUCCESS)) {
                // 遇到成功的就中断
                break;
            } else {
                // 还有reject的情况
            }
        }
        return errorCount.get();
    }
}
