package com.nb6868.onex.api.modules.msg.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.msg.dao.PushLogDao;
import com.nb6868.onex.api.modules.msg.dto.PushLogDTO;
import com.nb6868.onex.api.modules.msg.entity.PushLogEntity;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 消息推送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class PushLogService extends DtoService<PushLogDao, PushLogEntity, PushLogDTO> {

    @Override
    public QueryWrapper<PushLogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<PushLogEntity>(new QueryWrapper<>(), params)
                .like("content", "content")
                .like("params", "params")
                .like("result", "result")
                .eq("state", "state")
                .eq("alias", "alias")
                .eq("tags", "tags")
                .getQueryWrapper();
    }

}
