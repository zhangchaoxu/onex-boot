package com.nb6868.onexboot.api.modules.pay.dao;

import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.pay.entity.ChannelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付渠道
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface ChannelDao extends BaseDao<ChannelEntity> {

}
