package com.nb6868.onex.modules.pay.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.pay.dto.ChannelDTO;
import com.nb6868.onex.modules.pay.entity.ChannelEntity;

/**
 * 支付渠道
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ChannelService extends CrudService<ChannelEntity, ChannelDTO> {

    /**
     * 通过租户id和支付类型获得支付配置
     *
     * @param tenantId 租户id
     * @param payType  支付类型
     * @return 支付渠道
     */
    ChannelEntity getByTenantIdAndPayType(Long tenantId, String payType);

}
