package com.nb6868.onexboot.api.modules.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.pay.dao.ChannelDao;
import com.nb6868.onexboot.api.modules.pay.dto.ChannelDTO;
import com.nb6868.onexboot.api.modules.pay.entity.ChannelEntity;
import com.nb6868.onexboot.api.modules.pay.service.ChannelService;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.WrapperUtils;
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
        return query().eq("tenant_id", tenantId).eq("pay_type", payType).eq("state", 1).last(Const.LIMIT_ONE).one();
    }

}
