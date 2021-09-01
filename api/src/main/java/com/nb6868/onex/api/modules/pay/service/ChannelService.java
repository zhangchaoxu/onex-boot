package com.nb6868.onex.api.modules.pay.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.pay.dao.ChannelDao;
import com.nb6868.onex.api.modules.pay.dto.ChannelDTO;
import com.nb6868.onex.api.modules.pay.entity.ChannelEntity;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付渠道
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ChannelService extends DtoService<ChannelDao, ChannelEntity, ChannelDTO> {

    @Override
    public QueryWrapper<ChannelEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<ChannelEntity>(new QueryWrapper<>(), params)
                .eq("payType", "pay_type")
                .like("name", "name")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }
    /**
     * 通过租户id和支付类型获得支付配置
     *
     * @param tenantId 租户id
     * @param payType  支付类型
     * @return 支付渠道
     */
    public ChannelEntity getByTenantIdAndPayType(Long tenantId, String payType) {
        return query().eq("tenant_id", tenantId).eq("pay_type", payType).eq("state", 1).last(Const.LIMIT_ONE).one();
    }

}
