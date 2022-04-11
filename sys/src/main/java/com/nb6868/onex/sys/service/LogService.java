package com.nb6868.onex.sys.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.sys.dao.LogDao;
import com.nb6868.onex.sys.dto.LogDTO;
import com.nb6868.onex.sys.entity.LogEntity;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.log.BaseLogService;
import com.nb6868.onex.common.log.LogBody;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class LogService extends DtoService<LogDao, LogEntity, LogDTO> implements BaseLogService {

    @Override
    public QueryWrapper<LogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<LogEntity>(new QueryWrapper<>(), params)
                // 状态
                .eq("state", "state")
                // 类型
                .eq("type", "type")
                // 用户
                .like("createName", "create_name")
                // 请求uri
                .like("uri", "uri")
                // 创建时间区间
                .ge("startCreateTime", "create_time")
                .le("endCreateTime", "create_time")
                .getQueryWrapper();
    }

    @Override
    public void saveLog(LogBody log) {
        LogEntity logEntity = ConvertUtils.sourceToTarget(log, LogEntity.class);
        save(logEntity);
    }

    @Override
    public int getContinuousLoginErrorTimes(String user, String tenantCode, int minuteOffset, int limit) {
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
            if (log.getState() == ErrorCode.ACCOUNT_PASSWORD_ERROR) {
                errorCount++;
            } else {
                break;
            }
        }
        return errorCount;
    }
}
