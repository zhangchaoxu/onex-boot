package com.nb6868.onex.msg.mail;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.pojo.ApiResult;
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
 * 短信 阿里云 消息服务
 * see {<a href="https://help.aliyun.com/document_detail/59210.html">...</a>}
 * see {<a href="https://help.aliyun.com/zh/sms/developer-reference/api-dysmsapi-2017-05-25-sendsms">...</a>}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
@Service("SmsAliyunMailService")
public class SmsAliyunMailService extends AbstractMailService {

    @Override
    public boolean sendMail(MsgTplEntity mailTpl, MsgSendForm request) {
        // 检查模板参数
        AssertUtils.isTrue(null == mailTpl.getParams() || StrUtil.hasBlank(
                mailTpl.getParams().getStr("AppKeyId"),
                mailTpl.getParams().getStr("AppKeySecret"),
                mailTpl.getParams().getStr("TemplateId"),
                mailTpl.getParams().getStr("SignName")
        ), MsgConst.MAIL_TPL_PARAMS_ERROR);
        // 检查内容参数
        // 参数变量允许为空字符串,但是不允许为null,否则提示isv.INVALID_JSON_PARAM
        // 参数变量长度限制1-20字符以内,实际允许为0-20字符,中文数字字符均占1个字符,否则提示isv.PARAM_LENGTH_LIMIT
        ObjectUtil.defaultIfNull(request.getContentParams(), new JSONObject()).forEach((key, value) -> request.getContentParams().set(key, StrUtil.sub(ObjectUtil.defaultIfNull(value, " ").toString(), 0, 20)));
        // 消息记录
        MsgLogService mailLogService = SpringUtil.getBean(MsgLogService.class);
        MsgLogEntity mailLog = new MsgLogEntity();
        mailLog.setTenantCode(mailTpl.getTenantCode());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setMailFrom("sms_aliyun");
        mailLog.setMailTo(request.getMailTo());
        mailLog.setContentParams(request.getContentParams());
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.getCode());
        mailLog.setContent(StrUtil.format(mailTpl.getContent(), request.getContentParams(), true));
        mailLog.setState(MsgConst.MailSendStateEnum.SENDING.getCode());
        // 设置有效时间
        int validTimeLimit = mailTpl.getParams().getInt("validTimeLimit", 0);
        mailLog.setValidEndTime(validTimeLimit <= 0 ? DateUtil.offsetMonth(DateUtil.date(), 99 * 12) : DateUtil.offsetSecond(DateUtil.date(), validTimeLimit));
        // 先保存获得id,后续再更新状态和内容
        mailLogService.save(mailLog);

        // 封装阿里云接口参数,V2签名中对于sign字段的传参一致抽风，换V3了
        Map<String, Object> paras = new HashMap<>();
        paras.put("PhoneNumbers", request.getMailTo());
        paras.put("SignName", mailTpl.getParams().getStr("SignName"));
        paras.put("TemplateCode", mailTpl.getParams().getStr("TemplateId"));
        paras.put("TemplateParam", request.getContentParams().toString());
        ApiResult<JSONObject> resultJson = AliyunSmsApi.sendSms(mailTpl.getParams().getStr("AppKeyId"), mailTpl.getParams().getStr("AppKeySecret"), mailTpl.getParams().getStr("endPoint", "https://dysmsapi.aliyuncs.com/"), paras);
        mailLog.setResult(resultJson.getData().toString());
        mailLog.setState(resultJson.isSuccess() ? MsgConst.MailSendStateEnum.SUCCESS.getCode() : MsgConst.MailSendStateEnum.FAIL.getCode());
        mailLogService.updateById(mailLog);
        return mailLog.getState() == MsgConst.MailSendStateEnum.SUCCESS.getCode();
    }

}
