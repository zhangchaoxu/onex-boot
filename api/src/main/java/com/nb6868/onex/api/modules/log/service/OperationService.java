package com.nb6868.onex.api.modules.log.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.log.dao.OperationDao;
import com.nb6868.onex.api.modules.log.dto.OperationDTO;
import com.nb6868.onex.api.modules.log.entity.OperationEntity;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 操作日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OperationService extends DtoService<OperationDao, OperationEntity, OperationDTO> {

    @Override
    public QueryWrapper<OperationEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<OperationEntity>(new QueryWrapper<>(), params)
                // 状态
                .eq("state", "state")
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
