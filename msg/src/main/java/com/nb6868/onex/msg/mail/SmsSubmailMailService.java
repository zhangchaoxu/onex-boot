package com.nb6868.onex.msg.mail;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.msg.MsgSendReq;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.msg.MsgConst;
import com.nb6868.onex.msg.entity.MsgLogEntity;
import com.nb6868.onex.msg.entity.MsgTplEntity;
import com.nb6868.onex.msg.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信 赛邮云 消息服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
@Service("SmsSubmailMailService")
public class SmsSubmailMailService extends AbstractMailService {

    private static final String BASE_URL = "https://api-v4.mysubmail.com";

    @Override
    public boolean sendMail(MsgTplEntity mailTpl, MsgSendReq request) {
        AssertUtils.isTrue(null == mailTpl.getParams() || StrUtil.hasBlank(
                mailTpl.getParams().getStr("AppKeyId"),
                mailTpl.getParams().getStr("AppKeySecret"),
                mailTpl.getParams().getStr("TemplateId")
        ), MsgConst.MAIL_TPL_PARAMS_ERROR);
        // 消息记录
        MsgLogService mailLogService = SpringUtil.getBean(MsgLogService.class);
        MsgLogEntity mailLog = new MsgLogEntity();
        mailLog.setTenantCode(mailTpl.getTenantCode());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setMailFrom("sms_submail");
        mailLog.setMailTo(request.getMailTo());
        mailLog.setContent(StrUtil.format(mailTpl.getContent(), request.getContentParams(), true));
        mailLog.setContentParams(request.getContentParams());
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.getCode());
        mailLog.setState(MsgConst.MailSendStateEnum.SENDING.getCode());
        // 设置有效时间
        int validTimeLimit = mailTpl.getParams().getInt("validTimeLimit", 0);
        mailLog.setValidEndTime(validTimeLimit <= 0 ? DateUtil.offsetMonth(DateUtil.date(), 99 * 12) : DateUtil.offsetSecond(DateUtil.date(), validTimeLimit));
        // 先保存获得id,后续再更新状态和内容
        mailLogService.save(mailLog);
        // 调用接口发送
        try {
            // https://www.mysubmail.com/documents/OOVyh
            // https://api-v4.mysubmail.com/sms/xsend
            String url = BASE_URL + "/sms/xsend";
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("appid", mailTpl.getParams().getStr("AppKeyId"));
            paramMap.put("signature", mailTpl.getParams().getStr("AppKeySecret"));
            paramMap.put("to", request.getMailTo());
            paramMap.put("project", mailTpl.getParams().getStr("TemplateId"));
            paramMap.put("vars", request.getContentParams().toString());
            String result = HttpUtil.post(url, paramMap);
            JSONObject resultJson = JSONUtil.parseObj(result);
            mailLog.setResult(result);
            mailLog.setState((StrUtil.equalsIgnoreCase(resultJson.getStr("status"), "success") ? MsgConst.MailSendStateEnum.SUCCESS.getCode() : MsgConst.MailSendStateEnum.FAIL.getCode()));
        } catch (Exception e) {
            // 接口调用失败
            log.error("JuheSms", e);
            mailLog.setState(Const.ResultEnum.FAIL.getCode());
            mailLog.setResult(e.getMessage());
        }
        mailLogService.updateById(mailLog);
        return mailLog.getState() == MsgConst.MailSendStateEnum.SUCCESS.getCode();
    }
}
