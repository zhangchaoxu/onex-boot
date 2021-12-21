package com.nb6868.onex.msg.mail;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.dingtalk.BaseResponse;
import com.nb6868.onex.common.dingtalk.DingTalkApi;
import com.nb6868.onex.msg.dto.MailSendForm;
import com.nb6868.onex.msg.entity.MailTplEntity;

/**
 * 钉钉机器人消息
 * see {https://developers.dingtalk.com/document/robots/custom-robot-access}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class RobotDingtalkMailService extends AbstractMailService {

    @Override
    public boolean sendMail(MailTplEntity mailTpl, MailSendForm request) {
        JSONObject tplParam = JSONUtil.parseObj(mailTpl.getParam());
        JSONObject sendParam = JSONUtil.parseObj(request.getContentParam());
        String msgtype = sendParam.getStr("msgtype");
        String keywords = tplParam.getStr("keywords");
        if ("text".equalsIgnoreCase(msgtype)) {
            // text类型消息,要求有关键词,但实际未包含,补充上
            if (StrUtil.isNotBlank(keywords) && !StrUtil.contains(sendParam.getJSONObject("text").getStr("content"), keywords)) {
                sendParam.getJSONObject("text").set("content", keywords + "\n" + sendParam.getJSONObject("text").getStr("content"));
            }
        }
        // https://oapi.dingtalk.com/robot/send?access_token=xxxx
        BaseResponse sendResponse = DingTalkApi.sendRobotMsg(tplParam.getStr("access_token"), JSONUtil.toBean(sendParam, Dict.class));
        return sendResponse.isSuccess();
    }

}
