package com.nb6868.onex.sys.mail;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.dingtalk.BaseResponse;
import com.nb6868.onex.common.dingtalk.DingTalkApi;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.SpringContextUtils;
import com.nb6868.onex.sys.dto.MsgSendForm;
import com.nb6868.onex.sys.entity.MsgLogEntity;
import com.nb6868.onex.sys.entity.MsgTplEntity;
import com.nb6868.onex.sys.service.MsgLogService;

/**
 * 钉钉机器人消息
 * see {https://developers.dingtalk.com/document/robots/custom-robot-access}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class RobotDingtalkMailService extends AbstractMailService {

    @Override
    public boolean sendMail(MsgTplEntity mailTpl, MsgSendForm request) {
        String msgtype = request.getContentParams().getStr("msgtype");
        String keywords = mailTpl.getParams().getStr("keywords");
        if ("text".equalsIgnoreCase(msgtype)) {
            // text类型消息,要求有关键词,但实际未包含,补充上
            if (StrUtil.isNotBlank(keywords) && !StrUtil.contains(request.getContentParams().getJSONObject("text").getStr("content"), keywords)) {
                request.getContentParams().getJSONObject("text").set("content", keywords + "\n" + request.getContentParams().getJSONObject("text").getStr("content"));
            }
        }
        // https://oapi.dingtalk.com/robot/send?access_token=xxxx
        BaseResponse sendResponse = DingTalkApi.sendRobotMsg(mailTpl.getParams().getStr("access_token"), request.getContentParams());
        // 保存记录
        MsgLogEntity mailLog = new MsgLogEntity();
        mailLog.setTenantCode(mailTpl.getTenantCode());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setMailFrom("dingtalk_robot");
        mailLog.setMailTo(request.getMailTo());
        mailLog.setContentParams(request.getContentParams());
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());
        // 设置有效时间
        int timeLimit = mailTpl.getParams().getInt("timeLimit", -1);
        mailLog.setValidEndTime(timeLimit < 0 ? DateUtil.offsetMonth(DateUtil.date(), 99 * 12) : DateUtil.offsetSecond(DateUtil.date(), timeLimit));
        mailLog.setState(sendResponse.isSuccess() ? Const.ResultEnum.SUCCESS.value() : Const.ResultEnum.FAIL.value());
        MsgLogService mailLogService = SpringContextUtils.getBean(MsgLogService.class);
        mailLogService.save(mailLog);

        return sendResponse.isSuccess();
    }

}
