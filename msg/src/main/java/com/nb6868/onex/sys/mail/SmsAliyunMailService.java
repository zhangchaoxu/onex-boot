package com.nb6868.onex.sys.mail;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.*;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.sys.entity.MsgLogEntity;
import com.nb6868.onex.sys.entity.MsgTplEntity;
import com.nb6868.onex.sys.service.MsgLogService;
import com.nb6868.onex.sys.mail.sms.SmsProps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信 阿里云 消息服务
 * see {<a href="https://help.aliyun.com/document_detail/59210.html">...</a>}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class SmsAliyunMailService extends AbstractMailService {

    @Override
    public boolean sendMail(MsgTplEntity mailTpl, MsgSendForm request) {
        SmsProps smsProps = JSONUtil.toBean(mailTpl.getParams(), SmsProps.class);
        AssertUtils.isNull(smsProps, ErrorCode.PARAM_CFG_ERROR);

        // 参数变量允许为空字符串,但是不允许为null,否则提示isv.INVALID_JSON_PARAM
        // 参数变量长度限制1-20字符以内,实际允许为0-20字符,中文数字字符均占1个字符,否则提示isv.PARAM_LENGTH_LIMIT
        ObjectUtil.defaultIfNull(request.getContentParams(), new JSONObject())
                .forEach((key, value) -> request.getContentParams().set(key, StrUtil.sub(ObjectUtil.defaultIfNull(value, " ").toString(), 0, 20)));
        // 消息记录
        MsgLogService mailLogService = SpringContextUtils.getBean(MsgLogService.class);
        MsgLogEntity mailLog = new MsgLogEntity();
        mailLog.setTenantCode(mailTpl.getTenantCode());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setMailTo(request.getMailTo());
        mailLog.setContentParams(request.getContentParams());
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());
        mailLog.setContent(StrUtil.format(mailTpl.getContent(), request.getContentParams()));
        mailLog.setState(Const.ResultEnum.FAIL.value());
        // 设置有效时间
        int timeLimit = mailTpl.getParams().getInt("timeLimit", -1);
        mailLog.setValidEndTime(timeLimit < 0 ? DateUtil.offsetMonth(DateUtil.date(), 99 * 12) : DateUtil.offsetSecond(DateUtil.date(), timeLimit));
        // 先保存获得id,后续再更新状态和内容
        mailLogService.save(mailLog);

        // 封装阿里云接口参数
        Map<String, Object> paras = new HashMap<>();
        paras.put("SignatureMethod", "HMAC-SHA1");
        paras.put("SignatureNonce", IdUtil.fastUUID());
        paras.put("AccessKeyId", smsProps.getAppKey());
        paras.put("SignatureVersion", "1.0");
        // "yyyy-MM-dd'T'HH:mm:ss'Z'"
        paras.put("Timestamp", DateUtil.format(new Date(), DatePattern.UTC_FORMAT));
        paras.put("Format", "JSON");
        paras.put("Action", "SendSms");
        paras.put("Version", "2017-05-25");
        paras.put("RegionId", "cn-hangzhou");
        paras.put("PhoneNumbers", request.getMailTo());
        paras.put("SignName", smsProps.getSign());
        paras.put("TemplateParam", request.getContentParams());
        paras.put("TemplateCode", smsProps.getTplId());
        // 外部流水扩展字段
        paras.put("OutId", String.valueOf(mailLog.getId()));
        // 去除签名关键字Key
        paras.remove("Signature");
        String sortedQueryString = SignUtils.paramToQueryString(paras);
        // 参数签名
        String sign = SignUtils.urlEncode(SignUtils.signToBase64( "GET" + "&" + SignUtils.urlEncode("/") + "&" + SignUtils.urlEncode(sortedQueryString),smsProps.getAppSecret() + "&", "HmacSHA1"));
        // 调用接口发送
        try {
            // 直接get RestTemplate会将参数直接做UrlEncode,需要使用UriComponentsBuilder先build一下
            URI uri = UriComponentsBuilder.fromHttpUrl("http://dysmsapi.aliyuncs.com/?Signature=" + sign + "&" + sortedQueryString).build(true).toUri();
            String result = new RestTemplate().getForObject(uri, String.class);
            Map<String, Object> json = JacksonUtils.jsonToMap(result);
            mailLog.setResult(result);
            mailLog.setState("OK".equalsIgnoreCase(json.get("Code").toString()) ? Const.ResultEnum.SUCCESS.value() : Const.ResultEnum.FAIL.value());
        } catch (Exception e) {
            // 接口调用失败
            log.error("AliyunSms", e);
            mailLog.setState(Const.ResultEnum.FAIL.value());
            mailLog.setResult(e.getMessage());
        }

        mailLogService.updateById(mailLog);
        return mailLog.getState() == Const.ResultEnum.SUCCESS.value();
    }

}
