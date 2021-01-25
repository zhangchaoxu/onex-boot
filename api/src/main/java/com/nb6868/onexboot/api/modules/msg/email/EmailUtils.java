package com.nb6868.onexboot.api.modules.msg.email;

import com.nb6868.onexboot.api.common.util.TemplateUtils;
import com.nb6868.onexboot.api.modules.msg.dto.MailSendRequest;
import com.nb6868.onexboot.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onexboot.api.modules.msg.entity.MailTplEntity;
import com.nb6868.onexboot.api.modules.msg.service.MailLogService;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.util.DateUtils;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.StringUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 电子邮件工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
@Component
public class EmailUtils {

    @Autowired
    MailLogService mailLogService;

    /**
     * 实现邮件发送器
     *
     * @param props 配置参数
     * @return 邮件发送器
     */
    private JavaMailSenderImpl createMailSender(EmailProps props) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(props.getHost());
        sender.setPort(props.getPort());
        sender.setUsername(props.getUsername());
        sender.setPassword(props.getPassword());
        sender.setDefaultEncoding(StandardCharsets.UTF_8.name());
        Properties p = new Properties();
        p.setProperty("mail.smtp.timeout", "10000");
        p.setProperty("mail.smtp.auth", "false");
        sender.setJavaMailProperties(p);
        return sender;
    }

    /**
     * 发送邮件
     *
     * @param mailTpl 发送模板
     * @param request 邮件发送请求
     * @return 发送结果
     */
    public boolean sendMail(MailTplEntity mailTpl, MailSendRequest request) {
        // 序列化电子邮件配置
        EmailProps emailProps = JacksonUtils.jsonToPojo(mailTpl.getParam(), EmailProps.class, null);
        AssertUtils.isNull(emailProps, "模板为定义正确的电子邮件配置");
        // 组装标题和内容
        String title = TemplateUtils.getTemplateContent("mailTitle", mailTpl.getTitle(), JacksonUtils.jsonToMap(request.getTitleParam()));
        String content = TemplateUtils.getTemplateContent("mailContent", mailTpl.getContent(), JacksonUtils.jsonToMap(request.getContentParam()));
        // 创建发送器和邮件消息
        JavaMailSenderImpl mailSender = createMailSender(emailProps);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            messageHelper.setFrom(emailProps.getUsername());
            messageHelper.setTo(StringUtils.split(request.getMailTo(), ","));
            messageHelper.setCc(StringUtils.split(request.getMailCc(), ","));
            messageHelper.setSubject(title);
            messageHelper.setText(content, true);
            if (!ObjectUtils.isEmpty(request.getAttachments())) {
                for (File attachment : request.getAttachments()) {
                    messageHelper.addAttachment(MimeUtility.encodeWord(attachment.getName()), attachment);
                }
            }
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new OnexException("发送电子邮件失败");
        }

        Const.ResultEnum status = Const.ResultEnum.SUCCESS;
        //发送邮件
        try {
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            status = Const.ResultEnum.FAIL;
            log.error("send error", e);
        }

        MailLogEntity mailLog = new MailLogEntity();
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setTplType(mailTpl.getType());
        mailLog.setMailFrom(mailSender.getUsername());
        mailLog.setMailTo(request.getMailTo());
        mailLog.setMailCc(request.getMailCc());
        mailLog.setSubject(title);
        mailLog.setContent(content);
        mailLog.setStatus(status.value());
        mailLog.setConsumeStatus(Const.BooleanEnum.FALSE.value());
        // 设置有效时间
        if (mailTpl.getTimeLimit() > 0) {
            mailLog.setValidEndTime(DateUtils.addDateSeconds(DateUtils.now(), mailTpl.getTimeLimit()));
        }
        mailLogService.save(mailLog);
        return status == Const.ResultEnum.SUCCESS;
    }

}
