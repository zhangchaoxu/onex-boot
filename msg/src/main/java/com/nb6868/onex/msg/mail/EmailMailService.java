package com.nb6868.onex.msg.mail;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.extra.template.engine.freemarker.FreemarkerEngine;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.MultipartFileUtils;
import com.nb6868.onex.common.util.TemplateUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.msg.MsgConst;
import com.nb6868.onex.msg.entity.MsgLogEntity;
import com.nb6868.onex.msg.entity.MsgTplEntity;
import com.nb6868.onex.msg.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 电子邮件 消息服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
@Service("EmailMailService")
public class EmailMailService extends AbstractMailService {

    /**
     * 实现邮件发送器
     *
     * @param props 配置参数
     * @return 邮件发送器
     */
    private JavaMailSenderImpl createMailSender(JSONObject props) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(props.getStr("Host"));
        sender.setPort(props.getInt("Port", 25));
        sender.setUsername(props.getStr("Username"));
        sender.setPassword(props.getStr("Password"));
        sender.setDefaultEncoding(StandardCharsets.UTF_8.name());
        Properties p = new Properties();
        p.setProperty("mail.smtp.timeout", props.getStr("SMTPTimeout", "10000"));
        p.setProperty("mail.smtp.auth", props.getStr("SMTPAuth", "false"));
        sender.setJavaMailProperties(p);
        return sender;
    }

    @Override
    public boolean sendMail(MsgTplEntity mailTpl, MsgSendForm request) {
        AssertUtils.isTrue(null == mailTpl.getParams() || StrUtil.hasBlank(
                mailTpl.getParams().getStr("Host"),
                mailTpl.getParams().getStr("Username"),
                mailTpl.getParams().getStr("Password")
        ), MsgConst.MAIL_TPL_PARAMS_ERROR);
        // 组装标题和内容
        String title = TemplateUtils.renderRaw(mailTpl.getTitle(), request.getTitleParams(), FreemarkerEngine.class);
        String content = TemplateUtils.renderRaw(mailTpl.getContent(), request.getContentParams(), FreemarkerEngine.class);
        // 创建发送器和邮件消息
        JavaMailSenderImpl mailSender = createMailSender(mailTpl.getParams());
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 保存记录
        MsgLogService mailLogService = SpringUtil.getBean(MsgLogService.class);
        MsgLogEntity mailLog = new MsgLogEntity();
        mailLog.setTenantCode(mailTpl.getTenantCode());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setMailFrom(mailSender.getUsername());
        mailLog.setMailTo(request.getMailTo());
        mailLog.setMailCc(request.getMailCc());
        mailLog.setTitle(title);
        mailLog.setContent(content);
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());
        mailLog.setState(MsgConst.MailSendStateEnum.SENDING.value());
        // 设置有效时间
        int validTimeLimit = mailTpl.getParams().getInt("validTimeLimit", 0);
        mailLog.setValidEndTime(validTimeLimit <= 0 ? DateUtil.offsetMonth(DateUtil.date(), 99 * 12) : DateUtil.offsetSecond(DateUtil.date(), validTimeLimit));
        // 先保存获得id,后续再更新状态和内容
        mailLogService.save(mailLog);

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            messageHelper.setFrom(mailTpl.getParams().getStr("Username"));
            if (StrUtil.isNotBlank(request.getMailTo())) {
                messageHelper.setTo(StrUtil.splitToArray(request.getMailTo(), ',', -1));
            }
            if (StrUtil.isNotBlank(request.getMailCc())) {
                messageHelper.setCc(StrUtil.splitToArray(request.getMailCc(), ',', -1));
            }
            messageHelper.setSubject(title);
            messageHelper.setText(content, true);
            // 附件
            CollUtil.emptyIfNull(request.getAttachments()).forEach(fileBase64Form -> {
                if (StrUtil.isNotBlank(fileBase64Form.getFileBase64())) {
                    File file = MultipartFileUtils.base64ToFile(fileBase64Form.getFileBase64());
                    if (ObjectUtil.isNotNull(file)) {
                        try {
                            messageHelper.addAttachment(MimeUtility.encodeWord(fileBase64Form.getFilaName()), file);
                        } catch (Exception e) {
                            log.error("addAttachment error", e);
                        }
                    }
                }
            });
            //发送邮件
            mailSender.send(mimeMessage);
            // 保存记录
            mailLog.setState(MsgConst.MailSendStateEnum.SUCCESS.value());
        } catch (Exception e) {
            log.error("send error", e);
            mailLog.setState(MsgConst.MailSendStateEnum.FAIL.value());
            mailLog.setResult(e.getMessage());
        }

        mailLogService.updateById(mailLog);
        return mailLog.getState() == MsgConst.MailSendStateEnum.SUCCESS.value();
    }
}
