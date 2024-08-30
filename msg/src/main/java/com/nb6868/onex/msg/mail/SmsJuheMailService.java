package com.nb6868.onex.msg.mail;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.msg.MsgConst;
import com.nb6868.onex.msg.entity.MsgLogEntity;
import com.nb6868.onex.msg.entity.MsgTplEntity;
import com.nb6868.onex.msg.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 短信 聚合 消息服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
@Service("SmsJuheMailService")
public class SmsJuheMailService extends AbstractMailService {

    private static final String BASE_URL = "http://v.juhe.cn";

    @Override
    public boolean sendMail(MsgTplEntity mailTpl, MsgSendForm request) {
        AssertUtils.isTrue(null == mailTpl.getParams() || StrUtil.hasBlank(
                mailTpl.getParams().getStr("AppKeyId"),
                mailTpl.getParams().getStr("TemplateId")
        ), MsgConst.MAIL_TPL_PARAMS_ERROR);
        // 拼接参数
        StrJoiner paramJuhe = new StrJoiner("&");
        ObjectUtil.defaultIfNull(request.getContentParams(), new JSONObject()).forEach((key, value) -> {
            // 遍历json,拼装参数
            paramJuhe.append("#" + key + "#=" + value);
        });
        // 消息记录
        MsgLogService mailLogService = SpringUtil.getBean(MsgLogService.class);
        MsgLogEntity mailLog = new MsgLogEntity();
        mailLog.setTenantCode(mailTpl.getTenantCode());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setMailFrom("sms_juhe");
        mailLog.setMailTo(request.getMailTo());
        mailLog.setContent(StrUtil.format(mailTpl.getContent(), request.getContentParams()));
        mailLog.setContentParams(request.getContentParams());
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());
        mailLog.setState(MsgConst.MailSendStateEnum.SENDING.value());
        // 设置有效时间
        int validTimeLimit = mailTpl.getParams().getInt("validTimeLimit", 0);
        mailLog.setValidEndTime(validTimeLimit <= 0 ? DateUtil.offsetMonth(DateUtil.date(), 99 * 12) : DateUtil.offsetSecond(DateUtil.date(), validTimeLimit));
        // 先保存获得id,后续再更新状态和内容
        mailLogService.save(mailLog);
        // 调用接口发送
        try {
            // "http://v.juhe.cn/sms/send?key={1}&mobile={2}&tpl_id={3}&tpl_value={4}"
            String url = HttpUtil.urlWithFormUrlEncoded(BASE_URL + "/sms/send", Dict.create()
                    .set("key", mailTpl.getParams().getStr("AppKeyId"))
                    .set("tpl_id", mailTpl.getParams().getStr("TemplateId"))
                    .set("tpl_value", paramJuhe.toString())
                    .set("mobile", request.getMailTo()), Charset.defaultCharset());
            String result = HttpUtil.get(url);
            JSONObject resultJson = JSONUtil.parseObj(result);
            mailLog.setResult(result);
            mailLog.setState((resultJson.getInt("error_code") == 0 ? MsgConst.MailSendStateEnum.SUCCESS.value() : MsgConst.MailSendStateEnum.FAIL.value()));
        } catch (Exception e) {
            // 接口调用失败
            log.error("JuheSms", e);
            mailLog.setState(Const.ResultEnum.FAIL.value());
            mailLog.setResult(e.getMessage());
        }
        mailLogService.updateById(mailLog);
        return mailLog.getState() == MsgConst.MailSendStateEnum.SUCCESS.value();
    }

}
