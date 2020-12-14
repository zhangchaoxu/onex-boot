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
import com.nb6868.onexboot.api.modules.msg.sms.AbstractSmsService;
import com.nb6868.onexboot.api.modules.msg.sms.SmsFactory;
import com.nb6868.onexboot.api.modules.msg.sms.SmsProps;
import com.nb6868.onexboot.api.modules.uc.wx.WxProp;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.StringUtils;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.api.modules.msg.service.MailTplService;
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
                .eq("tplId", "tpl_id")
                .eq("tplCode", "tpl_code")
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
        return update().eq("id", id).set("consume_status", 1).update(new MailLogEntity());
    }

    @Override
    public MailLogEntity findLastLog(Long tplId, String mailTo) {
        return query().eq("tpl_id", tplId)
                .eq("mail_to", mailTo)
                .eq("status", 1)
                .eq("consume_status", 0)
                .orderByDesc("create_time")
                .last(Const.LIMIT_ONE)
                .one();
    }

    @Override
    public MailLogEntity findLastLogByTplCode(String tplCode, String mailTo) {
        return query().eq("tpl_code", tplCode)
                .eq("mail_to", mailTo)
                .eq("status", 1)
                .eq("consume_status", 0)
                .orderByDesc("create_time")
                .last(Const.LIMIT_ONE)
                .one();
    }

    /**
     * 发送消息
     */
    @Override
    public boolean send(MailSendRequest request) {
        if (request.getTplType().equalsIgnoreCase(MsgConst.MailTypeEnum.EMAIL.name())) {
            // 电子邮件
            return emailUtils.sendMail(request);
        } else if (request.getTplType().equalsIgnoreCase(MsgConst.MailTypeEnum.SMS.name())) {
            // 短信
            MailTplEntity mailTpl = mailTplService.getByTypeAndCode(request.getTplType(), request.getTplCode());
            AssertUtils.isNull(mailTpl, "找不到对应的消息模板:" + request.getTplCode());

            // 短信配置
            SmsProps smsProps = JacksonUtils.jsonToPojo(mailTpl.getParam(), SmsProps.class);
            AssertUtils.isNull(smsProps, ErrorCode.PARAM_CFG_ERROR);

            // 获取短信服务
            AbstractSmsService service = SmsFactory.build(smsProps.getPlatform());
            // 发送短信
            service.sendSms(mailTpl, request.getMailTo(), request.getContentParam());
            return true;
        } else if (request.getTplType().equalsIgnoreCase(MsgConst.MailTypeEnum.WX_MP_TEMPLATE.name())) {
            // 微信模板消息
            MailTplEntity mailTpl = mailTplService.getByTypeAndCode(request.getTplType(), request.getTplCode());
            AssertUtils.isNull(mailTpl, "找不到对应的消息模板:" + request.getTplCode());

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
                mailLog.setTplId(mailTpl.getId());
                mailLog.setTplCode(mailTpl.getCode());
                mailLog.setTplType(mailTpl.getType());
                mailLog.setContentParams(request.getContentParam());
                mailLog.setConsumeStatus(0);
                save(mailLog);
            }
            return true;
        } else if (request.getTplType().equalsIgnoreCase(MsgConst.MailTypeEnum.WX_MA_SUBSCRIBE.name())) {
            // 微信小程序模板消息
            MailTplEntity mailTpl = mailTplService.getByTypeAndCode(request.getTplType(), request.getTplCode());
            AssertUtils.isNull(mailTpl, "找不到对应的消息模板:" + request.getTplCode());

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
                mailLog.setTplId(mailTpl.getId());
                mailLog.setTplCode(mailTpl.getCode());
                mailLog.setTplType(mailTpl.getType());
                mailLog.setContentParams(request.getContentParam());
                mailLog.setConsumeStatus(0);
                save(mailLog);
            }
            return true;
        } else  {
            return false;
        }
    }

}
