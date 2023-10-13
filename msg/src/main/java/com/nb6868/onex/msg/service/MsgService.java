package com.nb6868.onex.msg.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.msg.BaseMsgService;
import com.nb6868.onex.common.msg.MsgLogBody;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.msg.MsgTplBody;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.msg.MsgConst;
import com.nb6868.onex.msg.entity.MsgLogEntity;
import com.nb6868.onex.msg.entity.MsgTplEntity;
import com.nb6868.onex.msg.mail.AbstractMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 消息服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
@Slf4j
public class MsgService implements BaseMsgService {

    @Autowired
    private MsgLogService msgLogService;
    @Autowired
    private MsgTplService msgTplService;

    /**
     * 消费消息记录
     */
    @Override
    public boolean consumeLog(Long logId) {
        return msgLogService.update().eq("id", logId).set("consume_state", Const.BooleanEnum.TRUE.value()).update(new MsgLogEntity());
    }

    /**
     * 获得最后一次发送记录
     */
    @Override
    public MsgLogBody getLatestByTplCode(String tenantCode, String tplCode, String mailTo) {
        MsgLogEntity msgLog = msgLogService.query().eq("tpl_code", tplCode)
                .eq("mail_to", mailTo)
                .eq(StrUtil.isNotBlank(tenantCode), "tenant_code", tenantCode)
                .eq("state", Const.BooleanEnum.TRUE.value())
                // .eq("consume_state", Const.BooleanEnum.FALSE.value())
                .orderByDesc("create_time")
                .last(Const.LIMIT_ONE)
                .one();
        return ConvertUtils.sourceToTarget(msgLog, MsgLogBody.class);
    }

    /**
     * 获得消息模板
     */
    @Override
    public MsgTplBody getTplByCode(String tenantCode, String tplCode) {
        return ConvertUtils.sourceToTarget(msgTplService.getByCode(tenantCode, tplCode), MsgTplBody.class);
    }

    /**
     * 发送消息
     */
    @Override
    public boolean sendMail(MsgSendForm sendForm) {
        MsgTplEntity mailTpl = msgTplService.getByCode(sendForm.getTenantCode(), sendForm.getTplCode());
        AssertUtils.isNull(mailTpl, ErrorCode.ERROR_REQUEST, "消息模板不存在");

        return send(mailTpl, sendForm);
    }

    /**
     * 验证消息
     */
    @Override
    public boolean verifyMailCode(String tenantCode, String tplCode, String mailTo, @NotNull String mailCode) {
        // 获得模板
        MsgTplBody msgTpl = getTplByCode(tenantCode, tplCode);
        AssertUtils.isNull(msgTpl, "消息模板不存在");
        // 看配置中是否存在白名单
        String mailToWhiteList = msgTpl.getParams().getStr("mailToWhiteList");
        String codeWhiteList = msgTpl.getParams().getStr("codeWhiteList");
        if (StrUtil.isAllNotBlank(mailToWhiteList, codeWhiteList) && ReUtil.isMatch(mailToWhiteList, mailTo) && ReUtil.isMatch(codeWhiteList, mailCode)) {
            return true;
        }
        // 获取最后一次短信记录
        MsgLogEntity lastSmsLog = msgLogService.query()
                .eq("tpl_code", tplCode)
                .eq("mail_to", mailTo)
                .eq(StrUtil.isNotBlank(tenantCode), "tenant_code", tenantCode)
                .eq("state", Const.BooleanEnum.TRUE.value())
                .eq("consume_state", Const.BooleanEnum.FALSE.value())
                .eq(StrUtil.format(Const.SQL_JSON_KEY, "content_params", msgTpl.getParams().getStr("codeKey", "code")), mailCode)
                .orderByDesc("create_time")
                .last(Const.LIMIT_ONE)
                .one();
        // 验证码是否正确
        AssertUtils.isNull(lastSmsLog, ErrorCode.ERROR_REQUEST, "验证码错误");
        // 验证码正确,校验过期时间
        AssertUtils.isTrue(lastSmsLog.getValidEndTime() != null && lastSmsLog.getValidEndTime().before(new Date()), ErrorCode.ERROR_REQUEST, "验证码已过期");
        // 将记录消费掉
        consumeLog(lastSmsLog.getId());
        return true;
    }

    /**
     * 发送消息
     */
    public boolean send(MsgTplEntity mailTpl, MsgSendForm sendForm) {
        // 检查消息模板是否有时间限制
        JSONObject tplParams = mailTpl.getParams();
        AssertUtils.isNull(tplParams, "模板配置参数不能为空");

        // 校验发送频次限时
        int timeLimit = tplParams.getInt("sendTimeLimit", 0);
        if (timeLimit > 0) {
            // 先校验该收件人是否timeLimit秒内发送过
            MsgLogBody lastMailLog = getLatestByTplCode(sendForm.getTenantCode(), sendForm.getTplCode(), sendForm.getMailTo());
            // 检查限定时间内是否已经发送
            AssertUtils.isTrue(null != lastMailLog && lastMailLog.getState() != MsgConst.MailSendStateEnum.FAIL.value() && DateUtil.between(DateUtil.date(), lastMailLog.getCreateTime(), DateUnit.SECOND) < timeLimit, ErrorCode.ERROR_REQUEST, "发送请求过于频繁,请稍后再试");
        }
        // 判断是否验证码消息类型
        if (mailTpl.getType() == MsgConst.MailTypeEnum.CODE.value()) {
            // 编码关键词
            JSONObject contentParams = Opt.ofNullable(sendForm.getContentParams()).orElse(new JSONObject());
            contentParams.set(tplParams.getStr("codeKey", "code"), RandomUtil.randomString(tplParams.getStr("codeBaseString", RandomUtil.BASE_NUMBER), tplParams.getInt("codeLength", 4)));
            sendForm.setContentParams(contentParams);
        }
        // 对于未定义的消息类型,需要实例化
        AbstractMailService mailService;
        String serviceName = StrUtil.format("{}{}MailService", StrUtil.nullToEmpty(StrUtil.upperFirst(StrUtil.toCamelCase(mailTpl.getChannel()))), StrUtil.nullToEmpty(StrUtil.upperFirst(StrUtil.toCamelCase(mailTpl.getPlatform()))));
        try {
            // 通过bean获取实现Service
            mailService = SpringUtil.getBean(serviceName, AbstractMailService.class);
        } catch (Exception e) {
            log.error("Msg Send Exception", e);
            throw new OnexException(ErrorCode.ERROR_REQUEST, "发送失败,请检查消息渠道与平台");
        }
        return mailService.sendMail(mailTpl, sendForm);
    }
}
