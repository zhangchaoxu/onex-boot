package com.nb6868.onexboot.api.modules.msg.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.common.util.TemplateUtils;
import com.nb6868.onexboot.api.modules.msg.MsgConst;
import com.nb6868.onexboot.api.modules.msg.dao.MailLogDao;
import com.nb6868.onexboot.api.modules.msg.dto.MailLogDTO;
import com.nb6868.onexboot.api.modules.msg.dto.MailSendRequest;
import com.nb6868.onexboot.api.modules.msg.email.EmailUtils;
import com.nb6868.onexboot.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onexboot.api.modules.msg.entity.MailTplEntity;
import com.nb6868.onexboot.api.modules.msg.service.MailLogService;
import com.nb6868.onexboot.api.modules.msg.service.MailTplService;
import com.nb6868.onexboot.api.modules.msg.sms.SmsFactory;
import com.nb6868.onexboot.api.modules.uc.wx.WxProp;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.StringUtils;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 邮件记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class MailLogServiceImpl extends CrudServiceImpl<MailLogDao, MailLogEntity, MailLogDTO> implements MailLogService {

    @Autowired
    MailTplService mailTplService;
    @Autowired
    EmailUtils emailUtils;

    @Override
    public QueryWrapper<MailLogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<MailLogEntity>(new QueryWrapper<>(), params)
                .eq("tplCode", "tpl_code")
                .eq("tplType", "tpl_type")
                .eq("mailTo", "mail_to")
                .eq("status", "status")
                .like("content", "content")
                .getQueryWrapper();
    }

    /**
     * 消费消息
     */
    @Override
    public boolean consumeById(Long id) {
        return update().eq("id", id).set("consume_status", Const.BooleanEnum.TRUE.value()).update(new MailLogEntity());
    }

    @Override
    public MailLogEntity findLastLogByTplCode(String tplCode, String mailTo) {
        return query().eq("tpl_code", tplCode)
                .eq("mail_to", mailTo)
                .eq("status", Const.BooleanEnum.TRUE.value())
                .eq("consume_status", Const.BooleanEnum.FALSE.value())
                .orderByDesc("create_time")
                .last(Const.LIMIT_ONE)
                .one();
    }

    /**
     * 发送消息
     */
    @Override
    public boolean send(MailSendRequest request) {
        MailTplEntity mailTpl = mailTplService.getByCode(request.getTplCode());
        AssertUtils.isNull(mailTpl, "未定义的消息模板:" + request.getTplCode());
        if (MsgConst.MailTypeEnum.EMAIL.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 邮件
            return emailUtils.sendMail(mailTpl, request);
        } else if (MsgConst.MailTypeEnum.SMS.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 短信
            // 获取短信服务，发送短信
            return SmsFactory.build(mailTpl.getPlatform()).sendSms(mailTpl, request.getMailTo(), request.getContentParam());
        } else if (MsgConst.MailTypeEnum.WX_MP_TEMPLATE.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 微信模板消息
            WxProp wxProp = JacksonUtils.jsonToPojo(mailTpl.getParam(), WxProp.class);
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
            List<String> openIds = StringUtils.splitToList(request.getMailTo());
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

                Const.ResultEnum status = Const.ResultEnum.FAIL;
                String result;
                try {
                    result = wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
                    status = Const.ResultEnum.SUCCESS;
                } catch (WxErrorException e) {
                    e.printStackTrace();
                    result = e.getError().getJson();
                }
                // 保存记录
                MailLogEntity mailLog = new MailLogEntity();
                mailLog.setMailTo(openId);
                mailLog.setStatus(status.value());
                mailLog.setResult(result);
                mailLog.setContent(content);
                mailLog.setTplCode(mailTpl.getCode());
                mailLog.setTplType(mailTpl.getType());
                mailLog.setContentParams(request.getContentParam());
                mailLog.setConsumeStatus(Const.BooleanEnum.FALSE.value());
                save(mailLog);
            }
            return true;
        } else if (MsgConst.MailTypeEnum.WX_MA_SUBSCRIBE.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 微信小程序模板消息
            WxProp wxProp = JacksonUtils.jsonToPojo(mailTpl.getParam(), WxProp.class);
            AssertUtils.isNull(wxProp, "消息模板配置错误");

            // 初始化service
            WxMaService wxService = new WxMaServiceImpl();
            WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
            config.setAppid(wxProp.getAppid());
            config.setSecret(wxProp.getSecret());
            config.setToken(wxProp.getToken());
            config.setAesKey(wxProp.getAesKey());
            config.setMsgDataFormat(wxProp.getMsgDataFormat());
            wxService.setWxMaConfig(config);

            // 可能是发送多个
            List<String> openIds = StringUtils.splitToList(request.getMailTo());
            for (String openId : openIds) {
                // 构建消息
                WxMaSubscribeMessage templateMessage = WxMaSubscribeMessage.builder()
                        .toUser(openId)
                        .templateId(wxProp.getTemplateId())
                        .build();

                // 封装消息实际内容
                Map<String, Object> contentParam = JacksonUtils.jsonToMap(request.getContentParam());
                String content = TemplateUtils.getTemplateContent("wxTemplateContent", mailTpl.getContent(), contentParam);

                for (String key : contentParam.keySet()) {
                    templateMessage.addData(new WxMaSubscribeMessage.Data(key, contentParam.get(key).toString()));
                }

                Const.ResultEnum status = Const.ResultEnum.FAIL;
                String result = "success";
                try {
                    wxService.getMsgService().sendSubscribeMsg(templateMessage);
                    status = Const.ResultEnum.SUCCESS;
                } catch (WxErrorException e) {
                    e.printStackTrace();
                    result = e.getError().getJson();
                }
                // 保存记录
                MailLogEntity mailLog = new MailLogEntity();
                mailLog.setMailTo(openId);
                mailLog.setStatus(status.value());
                mailLog.setResult(result);
                mailLog.setContent(content);
                mailLog.setTplCode(mailTpl.getCode());
                mailLog.setTplType(mailTpl.getType());
                mailLog.setContentParams(request.getContentParam());
                mailLog.setConsumeStatus(Const.BooleanEnum.FALSE.value());
                save(mailLog);
            }
            return true;
        } else  {
            return false;
        }
    }

}
