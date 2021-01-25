package com.nb6868.onexboot.api.modules.msg.service;

import com.nb6868.onexboot.api.modules.msg.dto.MailLogDTO;
import com.nb6868.onexboot.api.modules.msg.dto.MailSendRequest;
import com.nb6868.onexboot.common.service.CrudService;
import com.nb6868.onexboot.api.modules.msg.entity.MailLogEntity;

/**
 * 邮件发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface MailLogService extends CrudService<MailLogEntity, MailLogDTO> {

    /**
     * 发送消息
     *
     * @param sendRequest 发送请求
     * @return 发送结果
     */
    boolean send(MailSendRequest sendRequest);

    /**
     * 消费信息
     */
    boolean consumeById(Long id);


    /**
     * 通过模板编码和手机号找最后一次发送记录
     * @param tplCode 模板编码
     * @param mailTo 收件人
     * @return 记录
     */
    MailLogEntity findLastLogByTplCode(String tplCode, String mailTo);

}

