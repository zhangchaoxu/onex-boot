package com.nb6868.onex.msg.mail;

import com.nb6868.onex.msg.dto.MailSendRequest;
import com.nb6868.onex.msg.entity.MailTplEntity;

/**
 * 微信公众号模板消息 消息服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class WxMpTemplateMailService extends AbstractMailService {

    @Override
    public boolean sendMail(MailTplEntity mailTpl, MailSendRequest request) {
        /*WxMaService wxMaService = WechatMaPropsConfig.getService("");

        // 可能是发送多个
        List<String> openIds = StrSplitter.splitTrim(request.getMailTo(), ',', true);
        for (String openId : openIds) {
            // 构建消息
            WxMaSubscribeMessage templateMessage = WxMaSubscribeMessage.builder()
                    .toUser(openId)
                    //.templateId(request.getTemplateId())
                    .build();

            // 封装消息实际内容
            Map<String, Object> contentParam = JacksonUtils.jsonToMap(request.getContentParam());
            String content = TemplateUtils.getTemplateContent("wxTemplateContent", mailTpl.getContent(), contentParam);

            for (String key : contentParam.keySet()) {
                templateMessage.addData(new WxMaSubscribeMessage.MsgData(key, contentParam.get(key).toString()));
            }

            Const.ResultEnum state = Const.ResultEnum.FAIL;
            String result = "success";
            try {
                wxMaService.getMsgService().sendSubscribeMsg(templateMessage);
                state = Const.ResultEnum.SUCCESS;
            } catch (WxErrorException e) {
                e.printStackTrace();
                result = e.getError().getJson();
            }
            // 保存记录
            MailLogEntity mailLog = new MailLogEntity();
            mailLog.setMailTo(openId);
            mailLog.setState(state.value());
            mailLog.setResult(result);
            mailLog.setContent(content);
            mailLog.setTplCode(mailTpl.getCode());
            mailLog.setTplType(mailTpl.getType());
            mailLog.setContentParams(request.getContentParam());
            mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());
            save(mailLog);
        }*/
        return true;
    }

}
