package com.nb6868.onex.modules.aep.dao;

import com.nb6868.onex.booster.dao.BaseDao;
import com.nb6868.onex.modules.aep.entity.SubscriptionPushMessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * AEP-订阅消息通知
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface SubscriptionPushMessageDao extends BaseDao<SubscriptionPushMessageEntity> {

}
