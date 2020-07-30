package com.nb6868.onex.modules.msg.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.msg.dto.MailTplDTO;
import com.nb6868.onex.modules.msg.entity.MailTplEntity;

/**
 * 邮件模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface MailTplService extends CrudService<MailTplEntity, MailTplDTO> {

    /**
     * 通过类型和编码获取模板
     * @param code 模板编码
     * @param type 消息类型
     * @return 模板
     */
    MailTplEntity getByTypeAndCode(String type, String code);

}
