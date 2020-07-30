package com.nb6868.onex.modules.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.modules.shop.dao.OrderBenefitDao;
import com.nb6868.onex.modules.shop.dto.OrderBenefitDTO;
import com.nb6868.onex.modules.shop.entity.OrderBenefitEntity;
import com.nb6868.onex.modules.shop.service.OrderBenefitService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 订单收益明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OrderBenefitServiceImpl extends CrudServiceImpl<OrderBenefitDao, OrderBenefitEntity, OrderBenefitDTO> implements OrderBenefitService {

    @Override
    public QueryWrapper<OrderBenefitEntity> getWrapper(String method, Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<OrderBenefitEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

}
