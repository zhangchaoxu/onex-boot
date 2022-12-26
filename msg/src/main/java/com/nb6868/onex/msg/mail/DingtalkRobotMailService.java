package com.nb6868.onex.msg.mail;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.nb6868.onex.common.dingtalk.BaseResponse;
import com.nb6868.onex.common.dingtalk.DingTalkApi;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.msg.MsgConst;
import com.nb6868.onex.msg.entity.MsgLogEntity;
import com.nb6868.onex.msg.entity.MsgTplEntity;
import com.nb6868.onex.msg.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 钉钉机器人消息
 * see {<a href="https://developers.dingtalk.com/document/robots/custom-robot-access">...</a>}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
@Service("DingtalkRobotMailService")
public class DingtalkRobotMailService extends AbstractMailService {

    @Override
    public boolean sendMail(MsgTplEntity mailTpl, MsgSendForm request) {
        AssertUtils.isTrue(null == mailTpl.getParams() || StrUtil.hasBlank(
                mailTpl.getParams().getStr("AccessToken")
        ), MsgConst.MAIL_TPL_PARAMS_ERROR);
        String msgtype = request.getContentParams().getStr("msgtype");
        String keywords = mailTpl.getParams().getStr("Keywords");
        if ("text".equalsIgnoreCase(msgtype)) {
            // text类型消息,要求有关键词,但实际未包含,补充上
            if (StrUtil.isNotBlank(keywords) && !StrUtil.contains(request.getContentParams().getJSONObject("text").getStr("content"), keywords)) {
                request.getContentParams().getJSONObject("text").set("content", keywords + "\n" + request.getContentParams().getJSONObject("text").getStr("content"));
            }
        }
        // 保存记录
        MsgLogService mailLogService = SpringUtil.getBean(MsgLogService.class);
        MsgLogEntity mailLog = new MsgLogEntity();
        mailLog.setTenantCode(mailTpl.getTenantCode());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setMailFrom("dingtalk_robot");
        mailLog.setMailTo(request.getMailTo());
        mailLog.setContentParams(request.getContentParams());
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());
        mailLog.setState(MsgConst.MailSendStateEnum.SENDING.value());
        // 设置有效时间
        int validTimeLimit = mailTpl.getParams().getInt("validTimeLimit", 0);
        mailLog.setValidEndTime(validTimeLimit <= 0 ? DateUtil.offsetMonth(DateUtil.date(), 99 * 12) : DateUtil.offsetSecond(DateUtil.date(), validTimeLimit));
        mailLogService.save(mailLog);

        // https://oapi.dingtalk.com/robot/send?access_token=xxxx
        BaseResponse sendResponse = DingTalkApi.sendRobotMsg(mailTpl.getParams().getStr("AccessToken"), request.getContentParams());
        mailLog.setState(sendResponse.isSuccess() ? MsgConst.MailSendStateEnum.SUCCESS.value() : MsgConst.MailSendStateEnum.FAIL.value());
        mailLog.setResult(sendResponse.toString());
        mailLogService.updateById(mailLog);

        return sendResponse.isSuccess();
    }

}
