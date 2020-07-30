package com.nb6868.onex.modules.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.shop.dao.OrderLogDao;
import com.nb6868.onex.modules.shop.dto.OrderLogDTO;
import com.nb6868.onex.modules.shop.entity.OrderLogEntity;
import com.nb6868.onex.modules.shop.service.OrderLogService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 订单记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OrderLogServiceImpl extends CrudServiceImpl<OrderLogDao, OrderLogEntity, OrderLogDTO> implements OrderLogService {

    @Override
    public QueryWrapper<OrderLogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<OrderLogEntity>(new QueryWrapper<>(), params)
                .eq("id", "id")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

}
