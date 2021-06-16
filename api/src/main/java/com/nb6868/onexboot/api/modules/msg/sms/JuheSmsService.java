package com.nb6868.onexboot.api.modules.msg.sms;

import cn.hutool.core.util.StrUtil;
import com.nb6868.onexboot.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onexboot.api.modules.msg.entity.MailTplEntity;
import com.nb6868.onexboot.api.modules.msg.service.MailLogService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.SpringContextUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 聚合短信服务
 * see {https://www.juhe.cn/docs/api/id/54}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class JuheSmsService extends AbstractSmsService {

    public JuheSmsService() {
    }

    private static final String JUHE_SMS_SEND_URL = "http://v.juhe.cn/sms/send?key={1}&mobile={2}&tpl_id={3}&tpl_value={4}";

    @Override
    public boolean sendSms(MailTplEntity mailTpl, String phoneNumber, String params) {
        SmsProps smsProps = JacksonUtils.jsonToPojo(mailTpl.getParam(), SmsProps.class);
        AssertUtils.isNull(smsProps, ErrorCode.PARAM_CFG_ERROR);

        MailLogService mailLogService = SpringContextUtils.getBean(MailLogService.class);
        StringBuilder paramJuhe = new StringBuilder();
        String content = mailTpl.getContent();
        Map<String, Object> paramJson = JacksonUtils.jsonToMap(params, new HashMap<>());
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
        mailLog.setMailTo(phoneNumber);
        mailLog.setContent(content);
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setTplType(mailTpl.getType());
        mailLog.setContentParams(params);
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());

        // 调用接口发送
        Const.ResultEnum state = Const.ResultEnum.FAIL;
        RestTemplate restTemplate = new RestTemplate();
        String result;
        try {
            result = restTemplate.getForObject(JUHE_SMS_SEND_URL, String.class, smsProps.getAppKey(), phoneNumber, smsProps.getTplId(), URLEncoder.encode(paramJuhe.toString(), StandardCharsets.UTF_8.name()));
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

    @Override
    public boolean sendBatchSms(MailTplEntity mailTpl, String[] phoneNumbers, String params) {
        for (String phoneNumber : phoneNumbers) {
            sendSms(mailTpl, phoneNumber, params);
        }
        return true;
    }
}
