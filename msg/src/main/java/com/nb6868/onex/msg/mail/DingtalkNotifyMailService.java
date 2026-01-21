package com.nb6868.onex.msg.mail;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.msg.MsgSendReq;
import com.nb6868.onex.common.pojo.ApiResult;
import com.nb6868.onex.common.util.BaseApi;
import com.nb6868.onex.common.util.DingTalkApi;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.msg.MsgConst;
import com.nb6868.onex.msg.entity.MsgLogEntity;
import com.nb6868.onex.msg.entity.MsgTplEntity;
import com.nb6868.onex.msg.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 钉钉工作通知消息
 * see {<a href="https://open.dingtalk.com/document/orgapp-server/asynchronous-sending-of-enterprise-session-messages">...</a>}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
@Service("DingtalkNotifyMailService")
public class DingtalkNotifyMailService extends AbstractMailService {

    @Override
    public boolean sendMail(MsgTplEntity mailTpl, MsgSendReq request) {
        AssertUtils.isTrue(null == mailTpl.getParams() || StrUtil.hasBlank(
                mailTpl.getParams().getStr("AppKeyId"),
                mailTpl.getParams().getStr("AppKeySecret")
        ), MsgConst.MAIL_TPL_PARAMS_ERROR);

        JSONObject params = request.getContentParams().set("agent_id", mailTpl.getParams().getStr("AgentId"));
        // 保存记录
        MsgLogService mailLogService = SpringUtil.getBean(MsgLogService.class);
        MsgLogEntity mailLog = new MsgLogEntity();
        mailLog.setTenantCode(mailTpl.getTenantCode());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setMailFrom("dingtalk_notify");
        mailLog.setMailTo(request.getMailTo());
        mailLog.setContentParams(params);
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.getCode());
        mailLog.setState(MsgConst.MailSendStateEnum.SENDING.getCode());
        // 设置有效时间
        int validTimeLimit = mailTpl.getParams().getInt("validTimeLimit", 0);
        mailLog.setValidEndTime(validTimeLimit <= 0 ? DateUtil.offsetMonth(DateUtil.date(), 99 * 12) : DateUtil.offsetSecond(DateUtil.date(), validTimeLimit));
        mailLogService.save(mailLog);

        // 实例化dingtalk接口
        DingTalkApi dingTalkApi = DingTalkApi.of(mailTpl.getParams().getStr("AppKeyId"), mailTpl.getParams().getStr("AppKeySecret"))
                .setBaseUrl(mailTpl.getParams().getStr("BaseUrl"))
                .setBaseUrlV2(mailTpl.getParams().getStr("BaseUrlV2"))
                .setProxy(BaseApi.getProxy(mailTpl.getParams().getJSONObject("Proxy")));
        ApiResult<JSONObject> sendResult = dingTalkApi.sendNotifyMsg(params);
        mailLog.setState(sendResult.isSuccess() ? MsgConst.MailSendStateEnum.SUCCESS.getCode() : MsgConst.MailSendStateEnum.FAIL.getCode());
        mailLog.setResult(sendResult.getCodeMsg());
        mailLogService.updateById(mailLog);
        return sendResult.isSuccess();
    }

}
