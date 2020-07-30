package com.nb6868.onex.modules.msg.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.msg.dto.NoticeLogDTO;
import com.nb6868.onex.modules.msg.entity.NoticeLogEntity;

import java.util.List;

/**
 * 通知发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface NoticeLogService extends CrudService<NoticeLogEntity, NoticeLogDTO> {

    /**
     * 设置为已读
     * @param ids 对应的ids
     */
    boolean read(List<Long> ids);

    /**
     * 所有未读消息设置为已读
     */
    boolean readAllUnread();

}
