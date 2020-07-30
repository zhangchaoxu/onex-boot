package com.nb6868.onex.modules.msg.sms;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.exception.OnexException;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.util.JacksonUtils;
import com.nb6868.onex.booster.util.SpringContextUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.common.util.TemplateUtils;
import com.nb6868.onex.modules.msg.entity.MailLogEntity;
import com.nb6868.onex.modules.msg.entity.MailTplEntity;
import com.nb6868.onex.modules.msg.service.MailLogService;

import java.util.Map;

/**
 * 阿里云短信服务
 * see {https://help.aliyun.com/document_detail/101414.html}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
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

        //组装请求对象
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
        } catch (ClientException ce) {
            ce.printStackTrace();
            throw new OnexException(ErrorCode.SEND_SMS_ERROR);
        }

        // 封装短信实际内容
        String content = mailTpl.getContent();
        content = TemplateUtils.getTemplateContent("smsContent", content, JacksonUtils.jsonToMap(params));

        // 保存短信记录
        MailLogService mailLogService = SpringContextUtils.getBean(MailLogService.class);
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

}
