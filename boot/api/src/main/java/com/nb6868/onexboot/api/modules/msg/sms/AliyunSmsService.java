package com.nb6868.onexboot.api.modules.msg.sms;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.nb6868.onexboot.api.common.util.TemplateUtils;
import com.nb6868.onexboot.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onexboot.api.modules.msg.entity.MailTplEntity;
import com.nb6868.onexboot.api.modules.msg.service.MailLogService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.SpringContextUtils;
import com.nb6868.onexboot.common.util.StringUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 阿里云短信服务
 * see {https://help.aliyun.com/document_detail/101414.html}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class AliyunSmsService extends AbstractSmsService {

    private IAcsClient client;

    public AliyunSmsService() {

    }

    private void init(SmsProps smsProp) {
        DefaultProfile profile = DefaultProfile.getProfile(smsProp.getRegionId(), smsProp.getAppKey(), smsProp.getAppSecret());
        client = new DefaultAcsClient(profile);
    }

    @Override
    public boolean sendSms(MailTplEntity mailTpl, String phoneNumbers, String params) {
        SmsProps smsProps = JacksonUtils.jsonToPojo(mailTpl.getParam(), SmsProps.class);
        AssertUtils.isNull(smsProps, ErrorCode.PARAM_CFG_ERROR);
        init(smsProps);

        MailLogService mailLogService = SpringContextUtils.getBean(MailLogService.class);
        MailLogEntity mailLog = new MailLogEntity();
        mailLog.setMailTo(phoneNumbers);
        mailLog.setTplId(mailTpl.getId());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setTplType(mailTpl.getType());
        mailLog.setContentParams(params);
        mailLog.setConsumeStatus(0);
        // 封装短信实际内容
        mailLog.setContent(TemplateUtils.getTemplateContent("smsContent", mailTpl.getContent(), JacksonUtils.jsonToMap(params)));

        // 组装请求对象
        CommonRequest request = new CommonRequest();
        //request.setSysProtocol(ProtocolType.HTTPS);
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");

        // 短信模板-可在短信控制台中找到
        request.putQueryParameter("TemplateCode", smsProps.getTplId());
        request.putQueryParameter("RegionId", smsProps.getRegionId());
        // 短信签名-可在短信控制台中找到
        request.putQueryParameter("SignName", smsProps.getSign());
        // 接受手机号
        request.putQueryParameter("PhoneNumbers", phoneNumbers);
        // 参数
        request.putQueryParameter("TemplateParam", params);

        // todo 阿里云调用失败还是返回成功
        // 最后发送结果
        Const.ResultEnum status = Const.ResultEnum.FAIL;
        String result = "";
        CommonResponse response;
        try {
            response = client.getCommonResponse(request);
            if (response.getHttpStatus() == 200) {
                result = response.getData();
                Map<String, Object> json = JacksonUtils.jsonToMap(result);
                status = "OK".equalsIgnoreCase(json.get("Code").toString()) ? Const.ResultEnum.SUCCESS : Const.ResultEnum.FAIL;
            }
        } catch (ClientException e) {
            // 接口调用失败
            log.error("AliyunSms", e);
            mailLog.setStatus(status.value());
            mailLog.setResult(e.getMessage());
            mailLogService.save(mailLog);
            return false;
        }




        // 保存短信记录
        MailLogEntity mailLog = new MailLogEntity();
        mailLog.setMailTo(phoneNumbers);
        mailLog.setStatus(status.value());
        mailLog.setResult(result);
        mailLog.setContent(content);
        mailLog.setTplId(mailTpl.getId());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setTplType(mailTpl.getType());
        mailLog.setContentParams(params);
        mailLog.setConsumeStatus(0);
        mailLogService.save(mailLog);
        return status == Const.ResultEnum.SUCCESS;
    }

    @Override
    public boolean sendBatchSms(MailTplEntity mailTpl, String[] phoneNumbers, String params) {
        return sendSms(mailTpl, StringUtils.joinList(phoneNumbers), params);
    }
}
