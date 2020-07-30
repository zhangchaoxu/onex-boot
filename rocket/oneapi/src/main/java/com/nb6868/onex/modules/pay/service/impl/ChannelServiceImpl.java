package com.nb6868.onex.modules.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.pay.dao.ChannelDao;
import com.nb6868.onex.modules.pay.dto.ChannelDTO;
import com.nb6868.onex.modules.pay.entity.ChannelEntity;
import com.nb6868.onex.modules.pay.service.ChannelService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付渠道
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ChannelServiceImpl extends CrudServiceImpl<ChannelDao, ChannelEntity, ChannelDTO> implements ChannelService {

    @Override
    public QueryWrapper<ChannelEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<ChannelEntity>(new QueryWrapper<>(), params)
                .eq("payType", "pay_type")
                .like("name", "name")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    public ChannelEntity getByTenantIdAndPayType(Long tenantId, String payType) {
        return query().eq("tenant_id", tenantId).eq("pay_type", payType).eq("status", 1).last(Const.LIMIT_ONE).one();
    }

}
