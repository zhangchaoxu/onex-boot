package com.nb6868.onex.msg.mail;

import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.util.SpringContextUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.msg.dto.MailSendForm;
import com.nb6868.onex.msg.entity.MailLogEntity;
import com.nb6868.onex.msg.entity.MailTplEntity;
import com.nb6868.onex.msg.service.MailLogService;
import com.nb6868.onex.msg.mail.sms.SmsProps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信 聚合 消息服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class SmsJuheMailService extends AbstractMailService {

    private static final String JUHE_SMS_SEND_URL = "http://v.juhe.cn/sms/send?key={1}&mobile={2}&tpl_id={3}&tpl_value={4}";

    @Override
    public boolean sendMail(MailTplEntity mailTpl, MailSendForm request) {
        SmsProps smsProps = JacksonUtils.jsonToPojo(mailTpl.getParam(), SmsProps.class);
        AssertUtils.isNull(smsProps, ErrorCode.PARAM_CFG_ERROR);

        MailLogService mailLogService = SpringContextUtils.getBean(MailLogService.class);
        StringBuilder paramJuhe = new StringBuilder();
        String content = mailTpl.getContent();
        Map<String, Object> paramJson = JacksonUtils.jsonToMap(request.getContentParam(), new HashMap<>());
        for (String key : paramJson.keySet()) {
            // 遍历json,拼装参数
            if (StrUtil.isNotBlank(paramJuhe)) {
                paramJuhe.append("&");
            }
            paramJuhe.append("#").append(key).append("#=").append(paramJson.get(key));
            // 拼装短信内容
            content = content.replace("#" + key + "#", paramJson.get(key).toString());
        }
        // 发送记录记录
        MailLogEntity mailLog = new MailLogEntity();
        mailLog.setMailTo(request.getMailTo());
        mailLog.setContent(content);
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setTplType(mailTpl.getType());
        mailLog.setContentParams(request.getContentParam());
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());

        // 调用接口发送
        Const.ResultEnum state = Const.ResultEnum.FAIL;
        RestTemplate restTemplate = new RestTemplate();
        String result;
        try {
            result = restTemplate.getForObject(JUHE_SMS_SEND_URL, String.class, smsProps.getAppKey(), request.getMailTo(), smsProps.getTplId(), URLEncoder.encode(paramJuhe.toString(), StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            // 接口调用失败
            log.error("JuheSms", e);
            mailLog.setState(state.value());
            mailLog.setResult(e.getMessage());
            mailLogService.save(mailLog);
            return false;
        }

        Map<String, Object> json = JacksonUtils.jsonToMap(result);
        mailLog.setResult(result);
        mailLog.setState((int) json.get("error_code") == 0 ? Const.ResultEnum.SUCCESS.value() : Const.ResultEnum.FAIL.value());
        mailLogService.save(mailLog);
        return mailLog.getState() == Const.ResultEnum.SUCCESS.value();
    }

}
