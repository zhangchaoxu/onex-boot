package com.nb6868.onexboot.api.modules.msg.email;

import com.nb6868.onexboot.api.common.util.TemplateUtils;
import com.nb6868.onexboot.api.modules.msg.dto.MailSendRequest;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onexboot.api.modules.msg.entity.MailTplEntity;
import com.nb6868.onexboot.api.modules.msg.service.MailLogService;
import com.nb6868.onexboot.api.modules.msg.service.MailTplService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * 邮件工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Log4j2
@Component
public class EmailUtils {

    @Autowired
    private MailLogService mailLogService;
    @Autowired
    private MailTplService mailTplService;

    /**
     * 实现邮件发送器
     *
     * @param props 配置参数
     * @return 邮件发送器
     */
    private JavaMailSenderImpl createMailSender(EmailProps props) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(props.getSenderHost());
        sender.setPort(props.getSenderHostPort());
        sender.setUsername(props.getSenderUsername());
        sender.setPassword(props.getSenderPassword());
        sender.setDefaultEncoding("Utf-8");
        Properties p = new Properties();
        p.setProperty("mail.smtp.timeout", "10000");
        p.setProperty("mail.smtp.auth", "false");
        sender.setJavaMailProperties(p);
        return sender;
    }

    /**
     * 发送邮件
     *
     * @param request 邮件发送请求
     * @return true：成功   false：失败
     */
    public boolean sendMail(MailSendRequest request) {
        MailTplEntity mailTpl = mailTplService.getByTypeAndCode(request.getTplType(), request.getTplCode());
        AssertUtils.isNull(mailTpl, "找不到对应的消息模板:" + request.getTplCode());

        String title = TemplateUtils.getTemplateContent("mailTitle", mailTpl.getTitle(), JacksonUtils.jsonToMap(request.getTitleParam()));
        String content = TemplateUtils.getTemplateContent("mailContent", mailTpl.getContent(), JacksonUtils.jsonToMap(request.getContentParam()));

        EmailProps emailProps = JacksonUtils.jsonToPojo(mailTpl.getParam(), EmailProps.class, null);
        AssertUtils.isNull(emailProps, "模板为定义正确的电子邮件配置");

        JavaMailSenderImpl mailSender = createMailSender(emailProps);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            //设置utf-8编码
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setFrom(emailProps.getSenderUsername());
            //收件人
            messageHelper.setTo(StringUtils.split(request.getMailTo(), ","));
            //抄送
            messageHelper.setCc(StringUtils.split(request.getMailCc(), ","));
            // 主题
            messageHelper.setSubject(title);
            //邮件正文
            messageHelper.setText(content, true);
            // 附件
            if (ObjectUtils.isNotEmpty(request.getAttachments())) {
                for (File attachment : request.getAttachments()) {
                    messageHelper.addAttachment(MimeUtility.encodeWord(attachment.getName()), attachment);
                }
            }
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new OnexException("构建邮件发送器失败");
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
        mailLog.setTplId(mailTpl.getId());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setTplType(mailTpl.getType());
        mailLog.setMailFrom(emailProps.getSenderUsername());
        mailLog.setMailTo(request.getMailTo());
        mailLog.setSubject(title);
        mailLog.setContent(content);
        mailLog.setStatus(status.value());
        mailLogService.save(mailLog);

        return status == Const.ResultEnum.SUCCESS;
    }

}
