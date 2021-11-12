package com.nb6868.onex.msg.mail;

import com.nb6868.onex.msg.dto.MailSendRequest;
import com.nb6868.onex.msg.entity.MailTplEntity;

/**
 * 短信 华为云 消息服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class SmsHwcloudMailService extends AbstractMailService {

    @Override
    public boolean sendMail(MailTplEntity mailTpl, MailSendRequest request) {
        return false;
    }

}
