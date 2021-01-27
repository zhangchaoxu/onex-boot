package com.nb6868.onexboot.api.modules.msg.sms;

import com.nb6868.onexboot.api.common.util.TemplateUtils;
import com.nb6868.onexboot.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onexboot.api.modules.msg.entity.MailTplEntity;
import com.nb6868.onexboot.api.modules.msg.service.MailLogService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.AliSignUtils;
import com.nb6868.onexboot.common.util.SpringContextUtils;
import com.nb6868.onexboot.common.util.StringUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
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
public class AliyunSmsService extends AbstractSmsService {

    // 格式化时间参数
    private final java.text.SimpleDateFormat simpleDateFormat;

    public AliyunSmsService() {
        simpleDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        simpleDateFormat.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));
    }

    @Override
    public boolean sendSms(MailTplEntity mailTpl, String phoneNumbers, String params) {
        SmsProps smsProps = JacksonUtils.jsonToPojo(mailTpl.getParam(), SmsProps.class);
        AssertUtils.isNull(smsProps, ErrorCode.PARAM_CFG_ERROR);

        // 消息记录
        MailLogService mailLogService = SpringContextUtils.getBean(MailLogService.class);
        MailLogEntity mailLog = new MailLogEntity();
        mailLog.setMailTo(phoneNumbers);
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setTplType(mailTpl.getType());
        mailLog.setContentParams(params);
        mailLog.setConsumeStatus(Const.BooleanEnum.FALSE.value());
        mailLog.setContent(TemplateUtils.getTemplateContent("smsContent", mailTpl.getContent(), JacksonUtils.jsonToMap(params)));
        mailLog.setStatus(Const.ResultEnum.FAIL.value());
        // 先保存获得id,后续再更新状态和内容
        mailLogService.save(mailLog);

        // 封装阿里云接口参数
        Map<String, String> paras = new HashMap<>();
        paras.put("SignatureMethod", "HMAC-SHA1");
        paras.put("SignatureNonce", java.util.UUID.randomUUID().toString());
        paras.put("AccessKeyId", smsProps.getAppKey());
        paras.put("SignatureVersion", "1.0");
        paras.put("Timestamp", simpleDateFormat.format(new java.util.Date()));
        paras.put("Format", "JSON");
        paras.put("Action", "SendSms");
        paras.put("Version", "2017-05-25");
        paras.put("RegionId", "cn-hangzhou");
        paras.put("PhoneNumbers", phoneNumbers);
        paras.put("SignName", smsProps.getSign());
        paras.put("TemplateParam", params);
        paras.put("TemplateCode", smsProps.getTplId());
        // 外部流水扩展字段
        paras.put("OutId", String.valueOf(mailLog.getId()));
        // 去除签名关键字Key
        paras.remove("Signature");
        String sortedQueryString = AliSignUtils.paramToQueryString(paras);
        // 参数签名
        String sign = AliSignUtils.signature( "GET" + "&" + AliSignUtils.urlEncode("/") + "&" + AliSignUtils.urlEncode(sortedQueryString),smsProps.getAppSecret() + "&", "HmacSHA1");

        // 调用接口发送
        try {
            // 直接get RestTemplate会将参数直接做urlencode,需要使用UriComponentsBuilder先build一下
            URI uri = UriComponentsBuilder.fromHttpUrl("http://dysmsapi.aliyuncs.com/?Signature=" + sign + "&" + sortedQueryString).build(true).toUri();
            String result = new RestTemplate().getForObject(uri, String.class);
            Map<String, Object> json = JacksonUtils.jsonToMap(result);
            mailLog.setResult(result);
            mailLog.setStatus("OK".equalsIgnoreCase(json.get("Code").toString()) ? Const.ResultEnum.SUCCESS.value() : Const.ResultEnum.FAIL.value());
        } catch (Exception e) {
            // 接口调用失败
            log.error("AliyunSms", e);
            mailLog.setStatus(Const.ResultEnum.FAIL.value());
            mailLog.setResult(e.getMessage());
        }

        mailLogService.updateById(mailLog);
        return mailLog.getStatus() == Const.ResultEnum.SUCCESS.value();
    }

    @Override
    public boolean sendBatchSms(MailTplEntity mailTpl, String[] phoneNumbers, String params) {
        return sendSms(mailTpl, StringUtils.joinList(phoneNumbers), params);
    }

}
