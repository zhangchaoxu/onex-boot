package com.nb6868.onex.msg.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.util.SpringContextUtils;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.msg.MsgConst;
import com.nb6868.onex.msg.dao.MailLogDao;
import com.nb6868.onex.msg.dto.MailLogDTO;
import com.nb6868.onex.msg.dto.MailSendRequest;
import com.nb6868.onex.msg.entity.MailLogEntity;
import com.nb6868.onex.msg.entity.MailTplEntity;
import com.nb6868.onex.msg.mail.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 邮件发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class MailLogService extends DtoService<MailLogDao, MailLogEntity, MailLogDTO> {

    @Autowired
    private MailTplService mailTplService;

    @Override
    public QueryWrapper<MailLogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<MailLogEntity>(new QueryWrapper<>(), params)
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
        return update().eq("id", id).set("consume_state", Const.BooleanEnum.TRUE.value()).update(new MailLogEntity());
    }

    /**
     * 通过模板编码和手机号找最后一次发送记录
     * @param tplCode 模板编码
     * @param mailTo 收件人
     * @return 记录
     */
    public MailLogEntity findLastLogByTplCode(String tplCode, String mailTo) {
        return query().eq("tpl_code", tplCode)
                .eq("mail_to", mailTo)
                .eq("state", Const.BooleanEnum.TRUE.value())
                .eq("consume_state", Const.BooleanEnum.FALSE.value())
                .orderByDesc("create_time")
                .last(Const.LIMIT_ONE)
                .one();
    }

    /**
     * 发送消息
     */
    public boolean send(MailSendRequest request) {
        MailTplEntity mailTpl = mailTplService.getOneByColumn("code", request.getTplCode());
        AssertUtils.isNull(mailTpl, "未定义的消息模板:" + request.getTplCode());

        // 检查消息模板是否有时间限制
        if (mailTpl.getTimeLimit() > 0) {
            // 先校验该收件人是否timeLimit秒内发送过
            MailLogEntity lastMailLog = findLastLogByTplCode(request.getTplCode(), request.getMailTo());
            // 检查限定时间内是否已经发送
            AssertUtils.isTrue(null != lastMailLog && DateUtil.between(DateUtil.date(), lastMailLog.getCreateTime(), DateUnit.SECOND) < mailTpl.getTimeLimit(), ErrorCode.ERROR_REQUEST, "发送请求过于频繁");
        }
        // 判断是否验证码消息类型
        if (mailTpl.getType() == MsgConst.MailTypeEnum.CODE.value()) {
            request.setContentParam(JacksonUtils.pojoToJson(Dict.create().set("code", RandomUtil.randomNumbers(4))));
        }

        AbstractMailService mailService = null;
        if (MsgConst.MailChannelEnum.EMAIL.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 邮件
            mailService = new EmailMailService();
        } else if (MsgConst.MailChannelEnum.SMS.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 短信
            if ("aliyun".equalsIgnoreCase(mailTpl.getPlatform())) {
                mailService = new SmsAliyunMailService();
            } else if ("juhe".equalsIgnoreCase(mailTpl.getPlatform())) {
                mailService = new SmsJuheMailService();
            }
        } else if (MsgConst.MailChannelEnum.WX_MP_TEMPLATE.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 微信模板消息
            mailService = new WxMpTemplateMailService();
        } else if (MsgConst.MailChannelEnum.WX_MA_SUBSCRIBE.name().equalsIgnoreCase(mailTpl.getChannel())) {
            // 微信小程序模板消息
            mailService = new WxMaSubscribeMailService();
        }
        if (null != mailService) {
            return mailService.sendMail(mailTpl, request);
        } else {
            // 对于未定义的消息类型,需要实例化
            String serviceName = StrUtil.format("{}{}MailService", StrUtil.nullToEmpty(StrUtil.upperFirst(StrUtil.toCamelCase(mailTpl.getChannel()))), StrUtil.nullToEmpty(StrUtil.upperFirst(StrUtil.toCamelCase(mailTpl.getPlatform()))));
            // 通过bean获取实现Service
            Object target = SpringContextUtils.getBean(serviceName);
            // 通过反射执行run方法
            try {
                Method method = target.getClass().getDeclaredMethod("sendMail", MailTplEntity.class, MailSendRequest.class);
                return (boolean) method.invoke(target, mailTpl, request);
            } catch (Exception e) {
                log.error("发送消息发生错误", e);
                return false;
            }
        }
    }

}

