package com.nb6868.onexboot.api.modules.msg.sms;

import com.nb6868.onexboot.api.common.util.TemplateUtils;
import com.nb6868.onexboot.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onexboot.api.modules.msg.entity.MailTplEntity;
import com.nb6868.onexboot.api.modules.msg.service.MailLogService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.ParamParseUtils;
import com.nb6868.onexboot.common.util.StringUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云短信服务
 * see https://help.aliyun.com/document_detail/101414.html
 * see https://help.aliyun.com/document_detail/101485.htm
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
@Component
public class AliyunSmsService extends AbstractSmsService {

    @Autowired
    MailLogService mailLogService;

    @Override
    public boolean sendSms(MailTplEntity mailTpl, String phoneNumbers, String params) {
        SmsProps smsProps = JacksonUtils.jsonToPojo(mailTpl.getParam(), SmsProps.class);
        AssertUtils.isNull(smsProps, ErrorCode.PARAM_CFG_ERROR);

        MailLogEntity mailLog = new MailLogEntity();
        mailLog.setMailTo(phoneNumbers);
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setTplType(mailTpl.getType());
        mailLog.setContentParams(params);
        mailLog.setConsumeStatus(Const.BooleanEnum.FALSE.value());
        // 封装短信实际内容
        mailLog.setContent(TemplateUtils.getTemplateContent("smsContent", mailTpl.getContent(), JacksonUtils.jsonToMap(params)));

        Map<String, String> paras = new HashMap<>();
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        simpleDateFormat.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));
        // 1. 系统参数
        paras.put("SignatureMethod", "HMAC-SHA1");
        paras.put("SignatureNonce", java.util.UUID.randomUUID().toString());
        paras.put("AccessKeyId", smsProps.getAppKey());
        paras.put("SignatureVersion", "1.0");
        paras.put("Timestamp", simpleDateFormat.format(new java.util.Date()));
        paras.put("Format", "JSON");
        // 2. 业务API参数
        paras.put("Action", "SendSms");
        paras.put("Version", "2017-05-25");
        paras.put("RegionId", "cn-hangzhou");
        paras.put("PhoneNumbers", phoneNumbers);
        paras.put("SignName", smsProps.getSign());
        paras.put("TemplateParam", params);
        paras.put("TemplateCode", smsProps.getTplId());
        paras.put("OutId", "123");
        // 3. 去除签名关键字Key
        if (paras.containsKey("Signature")) {
            paras.remove("Signature");
        }
        String sortedQueryString = ParamParseUtils.paramToQueryString(paras);
        String sign = ParamParseUtils.sign(smsProps.getAppSecret() + "&", "GET" + "&" + ParamParseUtils.urlEncode("/") + "&" + ParamParseUtils.urlEncode(sortedQueryString), "HmacSHA1");

        // 调用接口发送
        Const.ResultEnum status = Const.ResultEnum.FAIL;
        RestTemplate restTemplate = new RestTemplate();
        String result;
        try {
            // 直接get RestTemplate会将参数直接做urlencode,需要使用UriComponentsBuilder先build一下
            result = restTemplate.getForObject(UriComponentsBuilder.fromHttpUrl("http://dysmsapi.aliyuncs.com/?Signature=" + ParamParseUtils.urlEncode(sign) + "&" + sortedQueryString).build(true).toUri(), String.class);
        } catch (Exception e) {
            // 接口调用失败
            log.error("AliyunSms", e);
            mailLog.setStatus(status.value());
            mailLog.setResult(e.getMessage());
            mailLogService.save(mailLog);
            return false;
        }
        Map<String, Object> json = JacksonUtils.jsonToMap(result);
        mailLog.setResult(result);
        mailLog.setStatus("OK".equalsIgnoreCase(json.get("Code").toString()) ? Const.ResultEnum.SUCCESS.value() : Const.ResultEnum.FAIL.value());
        mailLogService.save(mailLog);
        return mailLog.getStatus() == Const.ResultEnum.SUCCESS.value();
    }

    @Override
    public boolean sendBatchSms(MailTplEntity mailTpl, String[] phoneNumbers, String params) {
        return sendSms(mailTpl, StringUtils.joinList(phoneNumbers), params);
    }
}
