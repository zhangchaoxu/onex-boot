package com.nb6868.onex.sys.mail;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.dingtalk.BaseResponse;
import com.nb6868.onex.common.dingtalk.DingTalkApi;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.sys.entity.MsgLogEntity;
import com.nb6868.onex.sys.entity.MsgTplEntity;
import com.nb6868.onex.sys.service.MsgLogService;

/**
 * 钉钉工作通知消息
 * see {<a href="https://open.dingtalk.com/document/orgapp-server/asynchronous-sending-of-enterprise-session-messages">...</a>}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class DingtalkNotifyMailService extends AbstractMailService {

    @Override
    public boolean sendMail(MsgTplEntity mailTpl, MsgSendForm request) {
        JSONObject params = request.getContentParams().set("agent_id", mailTpl.getParams().getStr("agentId"));
        BaseResponse sendResponse = DingTalkApi.sendNotifyMsg(mailTpl.getParams().getStr("appId"), mailTpl.getParams().getStr("appSecret"), params);
        // 保存记录
        MsgLogEntity mailLog = new MsgLogEntity();
        mailLog.setTenantCode(mailTpl.getTenantCode());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setMailFrom("dingtalk_notify");
        mailLog.setMailTo(request.getMailTo());
        mailLog.setContentParams(request.getContentParams());
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());
        // 设置有效时间
        int timeLimit = mailTpl.getParams().getInt("timeLimit", -1);
        mailLog.setValidEndTime(timeLimit < 0 ? DateUtil.offsetMonth(DateUtil.date(), 99 * 12) : DateUtil.offsetSecond(DateUtil.date(), timeLimit));
        mailLog.setState(sendResponse.isSuccess() ? Const.ResultEnum.SUCCESS.value() : Const.ResultEnum.FAIL.value());
        MsgLogService mailLogService = SpringUtil.getBean(MsgLogService.class);
        mailLogService.save(mailLog);

        return sendResponse.isSuccess();
    }

}
