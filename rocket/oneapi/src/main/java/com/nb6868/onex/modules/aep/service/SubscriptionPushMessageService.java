package com.nb6868.onex.modules.aep.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.aep.dto.SubscriptionPushMessageDTO;
import com.nb6868.onex.modules.aep.entity.SubscriptionPushMessageEntity;

/**
 * AEP-订阅消息通知
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface SubscriptionPushMessageService extends CrudService<SubscriptionPushMessageEntity, SubscriptionPushMessageDTO> {

    /**
     * 订阅推送消息
     * @param message 消息
     * @return 处理结果
     */
    boolean notify(SubscriptionPushMessageDTO message);

}
