package com.nb6868.onex.uc;

import com.nb6868.onex.common.msg.BaseMsgService;
import com.nb6868.onex.common.msg.MsgLogBody;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.msg.MsgTplBody;
import org.springframework.stereotype.Service;

@Service
public class MsgService implements BaseMsgService {
    @Override
    public boolean consumeLog(Long logId) {
        return false;
    }

    @Override
    public MsgLogBody getLatestByTplCode(String tenantCode, String tplCode, String mailTo) {
        return null;
    }

    @Override
    public MsgTplBody getTplByCode(String tenantCode, String tplCode) {
        return null;
    }

    @Override
    public boolean sendMail(MsgSendForm form) {
        return false;
    }

    @Override
    public boolean verifyMailCode(String tenantCode, String tplCode, String mailTo, String mailCode) {
        return false;
    }
}
