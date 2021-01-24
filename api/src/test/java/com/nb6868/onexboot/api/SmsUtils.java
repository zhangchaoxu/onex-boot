package com.nb6868.onexboot.api;

import com.nb6868.onexboot.api.modules.msg.dto.MailSendRequest;
import com.nb6868.onexboot.api.modules.msg.service.MailLogService;
import com.nb6868.onexboot.common.util.ParamParseUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SmsUtils {

    @Autowired
    MailLogService mailLogService;

    @Test
    void sendSms() {
        MailSendRequest mailSendRequest = new MailSendRequest();
        mailSendRequest.setTplType("sms");
        mailSendRequest.setMailTo("13252421988");
        mailSendRequest.setTplCode("1273468852126146561");
        mailSendRequest.setContentParam("{\"code\":\"" + 123456 + "\"}");
        mailLogService.send(mailSendRequest);
    }

    @Test
    void sendAliyunSms() {
        RestTemplate restTemplate = new RestTemplate();
        String accessKeyId = "";
        String accessSecret = "";
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));// 这里一定要设置GMT时区
        java.util.Map<String, String> paras = new java.util.HashMap<String, String>();
        // 1. 系统参数
        paras.put("SignatureMethod", "HMAC-SHA1");
        paras.put("SignatureNonce", java.util.UUID.randomUUID().toString());
        paras.put("AccessKeyId", accessKeyId);
        paras.put("SignatureVersion", "1.0");
        paras.put("Timestamp", df.format(new java.util.Date()));
        paras.put("Format", "JSON");
        // 2. 业务API参数
        paras.put("Action", "SendSms");
        paras.put("Version", "2017-05-25");
        paras.put("RegionId", "cn-hangzhou");
        paras.put("PhoneNumbers", "");
        paras.put("SignName", "");
        paras.put("TemplateParam", "{\"code\":\"123456\"}");
        paras.put("TemplateCode", "SMS_146755096");
        paras.put("OutId", "123");
        // 3. 去除签名关键字Key
        if (paras.containsKey("Signature"))
            paras.remove("Signature");
        String sortedQueryString = ParamParseUtils.paramToQueryString(paras);
        String sign = ParamParseUtils.sign(accessSecret + "&", "GET" + "&" + ParamParseUtils.urlEncode("/") + "&" + ParamParseUtils.urlEncode(sortedQueryString), "HmacSHA1");
        // 最终打印出合法GET请求的URL
        String url = "http://dysmsapi.aliyuncs.com/?Signature=" + ParamParseUtils.urlEncode(sign) + "&" + sortedQueryString;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        System.out.println(url);
        String result = restTemplate.getForObject(builder.build(true).toUri(), String.class);
        System.out.println(result);
    }


}
