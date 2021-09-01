package com.nb6868.onex.api.modules.log.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.log.dao.ErrorDao;
import com.nb6868.onex.api.modules.log.dto.ErrorDTO;
import com.nb6868.onex.api.modules.log.entity.ErrorEntity;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 异常日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ErrorService extends DtoService<ErrorDao, ErrorEntity, ErrorDTO> {

    @Override
    public QueryWrapper<ErrorEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<ErrorEntity>(new QueryWrapper<>(), params)
                // 请求uri
                .like("uri", "uri")
                // 创建时间区间
                .ge("startCreateTime", "create_time")
                .le("endCreateTime", "create_time")
                .getQueryWrapper();
    }

}
