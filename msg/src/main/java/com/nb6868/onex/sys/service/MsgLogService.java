package com.nb6868.onex.sys.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.sys.MsgConst;
import com.nb6868.onex.sys.dao.MsgLogDao;
import com.nb6868.onex.sys.dto.MsgLogDTO;
import com.nb6868.onex.sys.entity.MsgLogEntity;
import com.nb6868.onex.sys.entity.MsgTplEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * 消息发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class MsgLogService extends DtoService<MsgLogDao, MsgLogEntity, MsgLogDTO> {

    @Autowired
    private MsgTplService msgTplService;

    /**
     * 消费消息
     */
    public boolean consumeById(Long id) {
        return update().eq("id", id).set("consume_state", Const.BooleanEnum.TRUE.value()).update(new MsgLogEntity());
    }

    /**
     * 通过模板编码和手机号找最后一次发送记录
     *
     * @param tplCode 模板编码
     * @param mailTo  收件人
     * @return 记录
     */
    public MsgLogEntity getLatestByTplCode(String tenantCode, String tplCode, String mailTo) {
        return query().eq("tpl_code", tplCode)
                .eq("mail_to", mailTo)
                .eq(StrUtil.isNotBlank(tenantCode), "tenant_code", tenantCode)
                .eq("state", Const.BooleanEnum.TRUE.value())
                // .eq("consume_state", Const.BooleanEnum.FALSE.value())
                .orderByDesc("create_time")
                .last(Const.LIMIT_ONE)
                .one();
    }

    /**
     * 发送消息
     */
    public boolean send(MsgSendForm sendForm) {
        MsgTplEntity mailTpl = msgTplService.getByCode(sendForm.getTenantCode(), sendForm.getTplCode());
        AssertUtils.isNull(mailTpl, ErrorCode.ERROR_REQUEST, "消息模板不存在");

        return send(mailTpl, sendForm);
    }

    public boolean send(MsgTplEntity mailTpl, MsgSendForm sendForm) {
        // 检查消息模板是否有时间限制
        int timeLimit = mailTpl.getParams().getInt("sendTimeLimit", 0);
        if (timeLimit > 0) {
            // 先校验该收件人是否timeLimit秒内发送过
            MsgLogEntity lastMailLog = getLatestByTplCode(sendForm.getTenantCode(), sendForm.getTplCode(), sendForm.getMailTo());
            // 检查限定时间内是否已经发送
            AssertUtils.isTrue(null != lastMailLog && lastMailLog.getState() != -1 && DateUtil.between(DateUtil.date(), lastMailLog.getCreateTime(), DateUnit.SECOND) < timeLimit, ErrorCode.ERROR_REQUEST, "发送请求过于频繁,请稍后再试");
        }
        // 判断是否验证码消息类型
        if (mailTpl.getType() == MsgConst.MailTypeEnum.CODE.value()) {
            // 编码关键词
            String codeKey =  mailTpl.getParams().getStr("codeKey", "code");
            if (sendForm.getContentParams() == null || StrUtil.isBlank(sendForm.getContentParams().getStr("codeKey"))) {
                String code = RandomUtil.randomString(mailTpl.getParams().getStr("codeBaseString", RandomUtil.BASE_NUMBER), mailTpl.getParams().getInt("codeLength", 4));
                sendForm.setContentParams(new JSONObject().set(codeKey, code));
            }
        }
        // 对于未定义的消息类型,需要实例化
        String serviceName = StrUtil.format("{}{}MailService", StrUtil.nullToEmpty(StrUtil.upperFirst(StrUtil.toCamelCase(mailTpl.getChannel()))), StrUtil.nullToEmpty(StrUtil.upperFirst(StrUtil.toCamelCase(mailTpl.getPlatform()))));
        try {
            // 通过bean获取实现Service
            Object target = SpringUtil.getBean(serviceName);
            // 通过反射执行run方法
            Method method = target.getClass().getDeclaredMethod("sendMail", MsgTplEntity.class, MsgSendForm.class);
            return (boolean) method.invoke(target, mailTpl, sendForm);
        } catch (Exception e) {
            log.error("发送消息发生错误", e);
            return false;
        }
    }

}

