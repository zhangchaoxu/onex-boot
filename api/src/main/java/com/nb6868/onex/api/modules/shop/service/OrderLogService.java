package com.nb6868.onex.api.modules.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.shop.dao.OrderLogDao;
import com.nb6868.onex.api.modules.shop.dto.OrderLogDTO;
import com.nb6868.onex.api.modules.shop.entity.OrderLogEntity;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 订单记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OrderLogService extends DtoService<OrderLogDao, OrderLogEntity, OrderLogDTO> {

    @Override
    public QueryWrapper<OrderLogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<OrderLogEntity>(new QueryWrapper<>(), params)
                .eq("id", "id")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

}
