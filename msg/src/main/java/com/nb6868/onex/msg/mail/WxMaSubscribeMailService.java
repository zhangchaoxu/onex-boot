package com.nb6868.onex.msg.mail;

import com.nb6868.onex.msg.dto.MailSendForm;
import com.nb6868.onex.msg.entity.MailTplEntity;

/**
 * 微信小程序订阅消息 消息服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class WxMaSubscribeMailService extends AbstractMailService {

    @Override
    public boolean sendMail(MailTplEntity mailTpl, MailSendForm request) {
        /*WxProp wxProp = JacksonUtils.jsonToPojo(mailTpl.getParam(), WxProp.class);
        AssertUtils.isNull(wxProp, "消息模板配置错误");

        // 初始化service
        WxMpService wxService = new WxMpServiceImpl();
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(wxProp.getAppid());
        config.setSecret(wxProp.getSecret());
        config.setToken(wxProp.getToken());
        config.setAesKey(wxProp.getAesKey());
        wxService.setWxMpConfigStorage(config);

        // 可能是发送多个
        List<String> openIds = StrSplitter.splitTrim(request.getMailTo(), ',', true);
        for (String openId : openIds) {
            // 构建消息
            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                    .toUser(openId)
                    .templateId(wxProp.getTemplateId())
                    .url("")
                    .build();

            // 封装消息实际内容
            Map<String, Object> contentParam = JacksonUtils.jsonToMap(request.getContentParam());
            String content = TemplateUtils.getTemplateContent("wxTemplateContent", mailTpl.getContent(), contentParam);

            for (String key : contentParam.keySet()) {
                templateMessage.addData(new WxMpTemplateData(key, contentParam.get(key).toString()));
            }

            Const.ResultEnum state = Const.ResultEnum.FAIL;
            String result;
            try {
                result = wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
                state = Const.ResultEnum.SUCCESS;
            } catch (WxErrorException e) {
                e.printStackTrace();
                result = e.getError().getJson();
            }
            // 保存记录
            MailLogEntity mailLog = new MailLogEntity();
            mailLog.setTenantCode(mailTpl.getTenantCode());
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
