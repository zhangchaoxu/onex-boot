package com.nb6868.onex.sys.mail;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.util.SpringContextUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.sys.dto.MsgSendForm;
import com.nb6868.onex.sys.entity.MsgLogEntity;
import com.nb6868.onex.sys.entity.MsgTplEntity;
import com.nb6868.onex.sys.mail.sms.SmsProps;
import com.nb6868.onex.sys.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    public boolean sendMail(MsgTplEntity mailTpl, MsgSendForm request) {
        SmsProps smsProps = JSONUtil.toBean(mailTpl.getParams(), SmsProps.class);
        AssertUtils.isNull(smsProps, ErrorCode.PARAM_CFG_ERROR);

        MsgLogService mailLogService = SpringContextUtils.getBean(MsgLogService.class);
        StrJoiner paramJuhe = new StrJoiner("&");
        request.getContentParams().forEach((key, value) -> {
            // 遍历json,拼装参数
            paramJuhe.append("#" + key + "#=" + value);
        });
        // 发送记录记录
        MsgLogEntity mailLog = new MsgLogEntity();
        mailLog.setTenantCode(mailTpl.getTenantCode());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setMailTo(request.getMailTo());
        mailLog.setContent(StrUtil.format(mailTpl.getContent(), request.getContentParams()));
        mailLog.setContentParams(request.getContentParams());
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());
        // 设置有效时间
        int timeLimit = mailTpl.getParams().getInt("timeLimit", -1);
        mailLog.setValidEndTime(timeLimit < 0 ? DateUtil.offsetMonth(DateUtil.date(), 99 * 12) : DateUtil.offsetSecond(DateUtil.date(), timeLimit));

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
