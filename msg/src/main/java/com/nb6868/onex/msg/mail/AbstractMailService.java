package com.nb6868.onex.msg.mail;

import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.msg.entity.MsgTplEntity;

/**
 * 消息服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public abstract class AbstractMailService {

    /**
     * 发送消息
     * @param mailTpl 消息模板
     * @param mailSendForm 消息请求
     * @return 发送结果
     */
    public abstract boolean sendMail(MsgTplEntity mailTpl, MsgSendForm mailSendForm);

}
