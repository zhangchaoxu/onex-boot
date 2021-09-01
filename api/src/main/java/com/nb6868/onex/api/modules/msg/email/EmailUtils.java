package com.nb6868.onex.api.modules.msg.email;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onex.api.modules.msg.entity.MailTplEntity;
import com.nb6868.onex.api.modules.msg.service.MailLogService;
import com.nb6868.onex.api.common.util.TemplateUtils;
import com.nb6868.onex.api.modules.msg.dto.MailSendRequest;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
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
        EmailProps emailProps = JSONUtil.toBean(mailTpl.getParam(), EmailProps.class);
        AssertUtils.isNull(emailProps, "电子邮件配置参数异常");
        // 组装标题和内容
        String title = TemplateUtils.getTemplateContent("mailTitle", mailTpl.getTitle(), JacksonUtils.jsonToMap(request.getTitleParam()));
        String content = TemplateUtils.getTemplateContent("mailContent", mailTpl.getContent(), JacksonUtils.jsonToMap(request.getContentParam()));
        // 创建发送器和邮件消息
        JavaMailSenderImpl mailSender = createMailSender(emailProps);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 保存邮件记录
        MailLogEntity mailLog = new MailLogEntity();
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setTplType(mailTpl.getType());
        mailLog.setMailFrom(mailSender.getUsername());
        mailLog.setMailTo(request.getMailTo());
        mailLog.setMailCc(request.getMailCc());
        mailLog.setSubject(title);
        mailLog.setContent(content);
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());
        // 设置有效时间
        if (mailTpl.getTimeLimit() > 0) {
            mailLog.setValidEndTime(DateUtil.offsetSecond(DateUtil.date(), mailTpl.getTimeLimit()));
        }
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            messageHelper.setFrom(emailProps.getUsername());
            messageHelper.setTo(StrSplitter.splitToArray(request.getMailTo(), ',', -1, true, true));
            messageHelper.setCc(StrSplitter.splitToArray(request.getMailCc(), ',', -1, true, true));
            messageHelper.setSubject(title);
            messageHelper.setText(content, true);
            // 附件
            if (!ObjectUtils.isEmpty(request.getAttachments())) {
                for (File attachment : request.getAttachments()) {
                    messageHelper.addAttachment(MimeUtility.encodeWord(attachment.getName()), attachment);
                }
            }
            //发送邮件
            mailSender.send(mimeMessage);
            // 保存记录
            mailLog.setState(Const.ResultEnum.SUCCESS.value());
        } catch (Exception e) {
            log.error("send error", e);
            mailLog.setState(Const.ResultEnum.FAIL.value());
        }
        mailLogService.save(mailLog);
        return mailLog.getState() == Const.ResultEnum.SUCCESS.value();
    }

}
