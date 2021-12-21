package com.nb6868.onex.msg.mail;

import com.nb6868.onex.msg.dto.MailSendForm;
import com.nb6868.onex.msg.entity.MailTplEntity;

/**
 * 消息服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public abstract class AbstractMailService {

    /**
     * 发送消息
     * @param mailTpl 消息模板
     * @param request 消息请求
     * @return 发送结果
     */
    public abstract boolean sendMail(MailTplEntity mailTpl, MailSendForm request);

}
