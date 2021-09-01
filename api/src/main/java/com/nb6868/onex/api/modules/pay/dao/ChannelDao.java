package com.nb6868.onex.api.modules.pay.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.api.modules.pay.entity.ChannelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付渠道
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface ChannelDao extends BaseDao<ChannelEntity> {

}
