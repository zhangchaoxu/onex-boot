package com.nb6868.onex.sys.service;

import com.nb6868.onex.common.msg.BaseMsgService;
import com.nb6868.onex.common.msg.MsgLogBody;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.msg.MsgTplBody;
import com.nb6868.onex.common.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MsgService implements BaseMsgService {

    @Autowired
    private MsgLogService msgLogService;
    @Autowired
    private MsgTplService msgTplService;

    @Override
    public boolean consumeLog(Long logId) {
        return msgLogService.consumeById(logId);
    }

    @Override
    public MsgLogBody getLatestByTplCode(String tenantCode, String tplCode, String mailTo) {
        return ConvertUtils.sourceToTarget(msgLogService.getLatestByTplCode(tenantCode, tplCode, mailTo), MsgLogBody.class);
    }

    @Override
    public MsgTplBody getTplByCode(String tenantCode, String tplCode) {
        return ConvertUtils.sourceToTarget(msgTplService.getByCode(tenantCode, tplCode), MsgTplBody.class);
    }

    @Override
    public boolean sendMail(MsgSendForm form) {
        return msgLogService.send(form);
    }

}
