package com.nb6868.onexboot.api.modules.msg.service;

import com.nb6868.onexboot.api.modules.msg.dto.NoticeLogDTO;
import com.nb6868.onexboot.common.service.CrudService;
import com.nb6868.onexboot.api.modules.msg.entity.NoticeLogEntity;

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
