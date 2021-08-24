package com.nb6868.onex.api.modules.msg.sms;

import com.nb6868.onex.api.modules.msg.entity.MailTplEntity;

/**
 * 短信
 * * 验证码类型短信,建议使用单独发送的方式
 * * 营销类型短信,建议使用批量发送的方式
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public abstract class AbstractSmsService {

    /**
     * 单条发送短信
     *
     * @param mailTpl     短信模板
     * @param phoneNumber 手机号
     * @param params      短信参数
     * @return 发送结果
     */
    public abstract boolean sendSms(MailTplEntity mailTpl, String phoneNumber, String params);

    /**
     * 批量发送短信
     * 阿里云支持多个手机号发送,记录一个结果
     * 聚合接口不支持,自己实现,记录多个结果,永远返回true
     *
     * @param mailTpl      短信模板
     * @param phoneNumbers 手机号，支持对多个手机号码发送短信，手机号码之间以英文逗号分隔。上限为1000个手机号码。批量调用相对于单条调用及时性稍有延迟。
     * @param params       短信参数
     * @return 发送结果
     */
    public abstract boolean sendBatchSms(MailTplEntity mailTpl, String[] phoneNumbers, String params);

}
