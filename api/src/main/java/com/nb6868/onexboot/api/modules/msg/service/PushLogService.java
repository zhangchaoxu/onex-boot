package com.nb6868.onexboot.api.modules.msg.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.msg.dao.PushLogDao;
import com.nb6868.onexboot.api.modules.msg.dto.PushLogDTO;
import com.nb6868.onexboot.api.modules.msg.entity.PushLogEntity;
import com.nb6868.onexboot.common.service.DtoService;
import com.nb6868.onexboot.common.util.ParamUtils;
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
    public QueryWrapper<PushLogEntity> getWrapper(String method, Map<String, Object> params){
        return new QueryWrapper<PushLogEntity>()
                .like(ParamUtils.isNotEmpty(params.get("content")), "content", params.get("content"))
                .like(ParamUtils.isNotEmpty(params.get("params")), "params", params.get("params"))
                .like(ParamUtils.isNotEmpty(params.get("result")), "result", params.get("result"))
                .eq(ParamUtils.isNotEmpty(params.get("state")), "state", params.get("state"))
                .eq(ParamUtils.isNotEmpty(params.get("alias")), "alias", params.get("alias"))
                .eq(ParamUtils.isNotEmpty(params.get("tags")), "tags", params.get("tags"));
    }

}
