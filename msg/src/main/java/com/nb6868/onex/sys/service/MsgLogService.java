package com.nb6868.onex.sys.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.sys.MsgConst;
import com.nb6868.onex.sys.dao.MsgLogDao;
import com.nb6868.onex.sys.dto.MsgLogDTO;
import com.nb6868.onex.sys.entity.MsgLogEntity;
import com.nb6868.onex.sys.entity.MsgTplEntity;
import com.nb6868.onex.sys.mail.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 消息发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class MsgLogService extends DtoService<MsgLogDao, MsgLogEntity, MsgLogDTO> {

    @Autowired
    private MsgTplService msgTplService;

    @Override
    public QueryWrapper<MsgLogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<MsgLogEntity>(new QueryWrapper<>(), params)
                .eq("tplCode", "tpl_code")
                .eq("tplType", "tpl_type")
                .like("mailTo", "mail_to")
                .like("mailCc", "mail_cc")
                .eq("state", "state")
                .like("content", "content")
                .getQueryWrapper();
    }

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
    public boolean send(MsgSendForm form) {
        MsgTplEntity mailTpl = msgTplService.getByCode(form.getTenantCode(), form.getTplCode());
        AssertUtils.isNull(mailTpl, ErrorCode.ERROR_REQUEST, "消息模板不存在");

        return send(mailTpl, form);
    }

    public boolean send(MsgTplEntity mailTpl, MsgSendForm form) {
        // 检查消息模板是否有时间限制
        int timeLimit = mailTpl.getParams().getInt("timeLimit", -1);
        if (timeLimit > 0) {
            // 先校验该收件人是否timeLimit秒内发送过
            MsgLogEntity lastMailLog = getLatestByTplCode(form.getTenantCode(), form.getTplCode(), form.getMailTo());
            // 检查限定时间内是否已经发送
            AssertUtils.isTrue(null != lastMailLog && DateUtil.between(DateUtil.date(), lastMailLog.getCreateTime(), DateUnit.SECOND) < timeLimit, ErrorCode.ERROR_REQUEST, "发送请求过于频繁");
        }
        // 判断是否验证码消息类型
        if (mailTpl.getType() == MsgConst.MailTypeEnum.CODE.value()) {
            // 编码关键词
            String codeKey =  mailTpl.getParams().getStr("codeKey", "code");
            if (form.getContentParams() == null || StrUtil.isBlank(form.getContentParams().getStr("codeKey"))) {
                String code = RandomUtil.randomString(mailTpl.getParams().getStr("codeBaseString", RandomUtil.BASE_NUMBER), mailTpl.getParams().getInt("codeLength", 4));
                form.setContentParams(new JSONObject().set(codeKey, code));
            }
        }
        AbstractMailService mailService = null;
        if (MsgConst.MailChannelEnum.EMAIL.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 邮件
            mailService = new EmailMailService();
        } else if (MsgConst.MailChannelEnum.SMS.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 短信
            if ("aliyun".equalsIgnoreCase(mailTpl.getPlatform())) {
                mailService = new SmsAliyunMailService();
            } else if ("hwcloud".equalsIgnoreCase(mailTpl.getPlatform())) {
                mailService = new SmsHwcloudMailService();
            } else if ("juhe".equalsIgnoreCase(mailTpl.getPlatform())) {
                mailService = new SmsJuheMailService();
            }
        } else if (MsgConst.MailChannelEnum.WX_MP_TEMPLATE.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 微信模板消息
            mailService = new WxMpTemplateMailService();
        } else if (MsgConst.MailChannelEnum.WX_MA_SUBSCRIBE.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 微信小程序模板消息
            mailService = new WxMaSubscribeMailService();
        } else if (MsgConst.MailChannelEnum.DINGTALK.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 钉钉
            if ("robot".equalsIgnoreCase(mailTpl.getPlatform())) {
                mailService = new DingtalkRobotMailService();
            } else if ("notify".equalsIgnoreCase(mailTpl.getPlatform())) {
                mailService = new DingtalkNotifyMailService();
            }
        }
        if (null != mailService) {
            return mailService.sendMail(mailTpl, form);
        } else {
            // 对于未定义的消息类型,需要实例化
            String serviceName = StrUtil.format("{}{}MailService", StrUtil.nullToEmpty(StrUtil.upperFirst(StrUtil.toCamelCase(mailTpl.getChannel()))), StrUtil.nullToEmpty(StrUtil.upperFirst(StrUtil.toCamelCase(mailTpl.getPlatform()))));
            // 通过bean获取实现Service
            Object target = SpringUtil.getBean(serviceName);
            // 通过反射执行run方法
            try {
                Method method = target.getClass().getDeclaredMethod("sendMail", MsgTplEntity.class, MsgSendForm.class);
                return (boolean) method.invoke(target, mailTpl, form);
            } catch (Exception e) {
                log.error("发送消息发生错误", e);
                return false;
            }
        }
    }

}

