package com.nb6868.onex.common.msg;

import org.springframework.stereotype.Service;

/**
 * 消息服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public interface BaseMsgService {

    /**
     * 消费记录
     */
    boolean consumeLog(Long logId);

    /**
     * 获得最后一次记录
     */
    MsgLogBody getLatestByTplCode(String tenantCode, String tplCode, String mailTo);

    /**
     * 通过编码获得模板
     */
    MsgTplBody getTplByCode(String tenantCode, String tplCode);

    /**
     * 发送消息
     */
    boolean sendMail(MsgSendForm form);

    /**
     * 验证消息验证码
     */
    boolean verifyMailCode(String tenantCode, String tplCode, String mailTo, String mailCode);

}
