package com.nb6868.onexboot.api.modules.log.service.impl;

import com.nb6868.onexboot.api.modules.log.dao.ErrorDao;
import com.nb6868.onexboot.api.modules.log.dto.ErrorDTO;
import com.nb6868.onexboot.api.modules.log.entity.ErrorEntity;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.api.modules.log.service.ErrorService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 异常日志
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Service
public class ErrorServiceImpl extends CrudServiceImpl<ErrorDao, ErrorEntity, ErrorDTO> implements ErrorService {

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
