package com.nb6868.onex.msg.mail;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.msg.MsgConst;
import com.nb6868.onex.msg.entity.MsgLogEntity;
import com.nb6868.onex.msg.entity.MsgTplEntity;
import com.nb6868.onex.msg.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 微信公众号模板消息 消息服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
@Service("WxMpTemplateMailService")
public class WxMpTemplateMailService extends AbstractMailService {

    @Override
    public boolean sendMail(MsgTplEntity mailTpl, MsgSendForm request) {
        AssertUtils.isTrue(null == mailTpl.getParams() || StrUtil.hasBlank(
                mailTpl.getParams().getStr("AppId"),
                mailTpl.getParams().getStr("AppSecret"),
                mailTpl.getParams().getStr("TemplateId")
        ), MsgConst.MAIL_TPL_PARAMS_ERROR);

        // 初始化service
        WxMpService wxService = new WxMpServiceImpl();
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(mailTpl.getParams().getStr("AppId"));
        config.setSecret(mailTpl.getParams().getStr("AppSecret"));
        wxService.setWxMpConfigStorage(config);

        // 可能是发送多个
        List<String> openIds = StrUtil.splitTrim(request.getMailTo(), ',');
        for (String openId : openIds) {
            // 消息记录
            MsgLogService mailLogService = SpringUtil.getBean(MsgLogService.class);
            MsgLogEntity mailLog = new MsgLogEntity();
            mailLog.setTenantCode(mailTpl.getTenantCode());
            mailLog.setTplCode(mailTpl.getCode());
            mailLog.setMailFrom("wx_mp_template");
            mailLog.setMailTo(openId);
            mailLog.setTitle(mailTpl.getTitle());
            mailLog.setContentParams(request.getContentParams());
            mailLog.setContent(request.getContentParams().toString());
            mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());
            mailLog.setState(MsgConst.MailSendStateEnum.SENDING.value());
            // 设置有效时间
            int validTimeLimit = mailTpl.getParams().getInt("validTimeLimit", 0);
            mailLog.setValidEndTime(validTimeLimit <= 0 ? DateUtil.offsetMonth(DateUtil.date(), 99 * 12) : DateUtil.offsetSecond(DateUtil.date(), validTimeLimit));
            // 先保存获得id,后续再更新状态和内容
            mailLogService.save(mailLog);

            // 构建消息
            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                    .toUser(openId)
                    .templateId(mailTpl.getParams().getStr("TemplateId"))
                    // 用mail log id作为防重id
                    .clientMsgId(mailLog.getId().toString())
                    .build();

            // 处理小程序或者url链接的跳转
            if (request.getExtParams() != null) {
                String url = request.getExtParams().getStr("url");
                String miniProgramAppId = request.getExtParams().getStr("miniProgramAppId");
                String miniProgramPagePath = request.getExtParams().getStr("miniProgramPagePath");
                boolean miniProgramUsePath = request.getExtParams().getBool("miniProgramUsePath", false);
                if (StrUtil.isAllNotBlank(miniProgramAppId, miniProgramPagePath)) {
                    templateMessage.setMiniProgram(new WxMpTemplateMessage.MiniProgram(miniProgramAppId, miniProgramPagePath, miniProgramUsePath));
                }
                if (StrUtil.isNotBlank(url)) {
                    templateMessage.setUrl(url);
                }
            }

            // 填充数据内容
            request.getContentParams().forEach((key, value) -> templateMessage.addData(new WxMpTemplateData(key, StrUtil.toStringOrNull(value))));
            // 发送消息
            try {
                String sendResult = wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
                mailLog.setResult(sendResult);
            } catch (WxErrorException e) {
                e.printStackTrace();
            }
            mailLogService.updateById(mailLog);
        }
        return true;
    }

}
