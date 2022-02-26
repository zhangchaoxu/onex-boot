package com.nb6868.onex.msg.mail;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.util.SpringContextUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.msg.dto.MailSendForm;
import com.nb6868.onex.msg.entity.MailLogEntity;
import com.nb6868.onex.msg.entity.MailTplEntity;
import com.nb6868.onex.msg.mail.sms.SmsProps;
import com.nb6868.onex.msg.service.MailLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信 华为云 消息服务
 * see {https://support.huaweicloud.com/devg-msgsms/sms_04_0002.html}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class SmsHwcloudMailService extends AbstractMailService {

    // 无需修改,用于格式化鉴权头域,给"X-WSSE"参数赋值
    private static final String WSSE_HEADER_FORMAT = "UsernameToken Username=\"{}\",PasswordDigest=\"{}\",Nonce=\"{}\",Created=\"{}\"";
    // 无需修改,用于格式化鉴权头域,给"Authorization"参数赋值
    private static final String AUTH_HEADER_VALUE = "WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"";

    @Override
    public boolean sendMail(MailTplEntity mailTpl, MailSendForm request) {
        SmsProps smsProps = JacksonUtils.jsonToPojo(mailTpl.getParam(), SmsProps.class);
        AssertUtils.isNull(smsProps, ErrorCode.PARAM_CFG_ERROR);

        // 参数变量允许为空字符串,但是不允许为null,否则提示isv.INVALID_JSON_PARAM
        // 参数变量长度限制1-20字符以内,实际允许为0-20字符,中文数字字符均占1个字符,否则提示isv.PARAM_LENGTH_LIMIT
        Map<String, Object> paramMap = JacksonUtils.jsonToMap(request.getContentParam());
        JSONArray paramArray = new JSONArray();
        paramMap.forEach((key, value) -> {
            if (null == value) {
                // 不允许为空,为空则替换为
                paramMap.put(key, "");
                paramArray.add("");
            } else if (value.toString().length() > 20) {
                // 超过20的，截取长度
                paramMap.put(key, value.toString().substring(0, 19) + "…");
                paramArray.add(value.toString().substring(0, 19) + "…");
            } else {
                paramArray.add(value.toString());
            }
        });

        // 消息记录
        MailLogService mailLogService = SpringContextUtils.getBean(MailLogService.class);
        MailLogEntity mailLog = new MailLogEntity();
        mailLog.setMailTo(request.getMailTo());
        mailLog.setContent(mailTpl.getContent());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setTplType(mailTpl.getType());
        mailLog.setContentParams(request.getContentParam());
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());
        mailLog.setContent(StrUtil.format(mailTpl.getContent(), paramMap));
        mailLog.setState(Const.ResultEnum.FAIL.value());
        // 先保存获得id,后续再更新状态和内容
        mailLogService.save(mailLog);

        //必填,请参考"开发准备"获取如下数据,替换为实际值
        //APP接入地址(在控制台"应用管理"页面获取)+接口访问URI
        String appKey = smsProps.getAppKey(); //APP_Key
        String appSecret = smsProps.getAppSecret(); //APP_Secret
        String sender = smsProps.getChannelId(); //国内短信签名通道号或国际/港澳台短信通道号
        String templateId = smsProps.getTplId(); //模板ID
        //条件必填,国内短信关注,当templateId指定的模板类型为通用模板时生效且必填,必须是已审核通过的,与模板类型一致的签名名称
        //国际/港澳台短信不用关注该参数
        String signature = smsProps.getSign(); //签名名称
        //必填,全局号码格式(包含国家码),示例:+8615123456789,多个号码之间用英文逗号分隔
        String receiver = request.getMailTo(); //短信接收人号码
        //选填,短信状态报告接收地址,推荐使用域名,为空或者不填表示不接收状态报告
        String statusCallBack = "";

        /**
         * 选填,使用无变量模板时请赋空值 String templateParas = "";
         * 单变量模板示例:模板内容为"您的验证码是${1}"时,templateParas可填写为"[\"369751\"]"
         * 双变量模板示例:模板内容为"您有${1}件快递请到${2}领取"时,templateParas可填写为"[\"3\",\"人民公园正门\"]"
         * 模板中的每个变量都必须赋值，且取值不能为空
         * 查看更多模板和变量规范:产品介绍>模板和变量规范
         */
        //模板变量，此处以单变量验证码短信为例，请客户自行生成6位验证码，并定义为字符串类型，以杜绝首位0丢失的问题（例如：002569变成了2569）。
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", AUTH_HEADER_VALUE);
        //请求Headers中的X-WSSE参数值
        headers.add("X-WSSE", buildWsseHeader(appKey, appSecret));
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("from", sender);
        postParameters.add("to", receiver);
        postParameters.add("templateId", templateId);
        if (!paramArray.isEmpty()) {
            postParameters.add("templateParas", paramArray.toString());
        }
        if (StrUtil.isNotBlank(statusCallBack)) {
            postParameters.add("statusCallback", statusCallBack);
        }
        if (StrUtil.isNotBlank(signature)) {
            postParameters.add("signature", signature);
        }
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(postParameters, headers);
        try {
            // 设置链接超时
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            String result = new RestTemplate(factory).postForObject(smsProps.getRegionId() + "/sms/batchSendSms/v1", httpEntity, String.class);

            Map<String, Object> json = JacksonUtils.jsonToMap(result);
            mailLog.setResult(result);
            mailLog.setState("000000".equalsIgnoreCase(MapUtil.getStr(json, "code")) ? Const.ResultEnum.SUCCESS.value() : Const.ResultEnum.FAIL.value());
        } catch (Exception e) {
            log.error("HwcloudSms", e);
            mailLog.setState(Const.ResultEnum.FAIL.value());
            mailLog.setResult(e.getMessage());
        }

        mailLogService.updateById(mailLog);
        return mailLog.getState() == Const.ResultEnum.SUCCESS.value();
    }

    /**
     * 构造X-WSSE参数值
     */
    private static String buildWsseHeader(String appKey, String appSecret) {
        String time = DateUtil.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss'Z'");
        String nonce = IdUtil.fastSimpleUUID();
        String passwordDigestBase64Str = Base64.encode(SecureUtil.sha256(nonce + time + appSecret));
        return StrUtil.format(WSSE_HEADER_FORMAT, appKey, passwordDigestBase64Str, nonce, time);
    }

}
