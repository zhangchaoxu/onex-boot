package com.nb6868.onex.modules.log.service.impl;

import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.log.dao.OperationDao;
import com.nb6868.onex.modules.log.dto.OperationDTO;
import com.nb6868.onex.modules.log.entity.OperationEntity;
import com.nb6868.onex.modules.log.service.OperationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 操作日志
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Service
public class OperationServiceImpl extends CrudServiceImpl<OperationDao, OperationEntity, OperationDTO> implements OperationService {

    @Override
    public QueryWrapper<OperationEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<OperationEntity>(new QueryWrapper<>(), params)
                // 状态
                .eq("status", "status")
                // 用户
                .like("createName", "create_name")
                // 请求uri
                .like("uri", "uri")
                // 创建时间区间
                .ge("startCreateTime", "create_time")
                .le("endCreateTime", "create_time")
                .getQueryWrapper();
    }

}
