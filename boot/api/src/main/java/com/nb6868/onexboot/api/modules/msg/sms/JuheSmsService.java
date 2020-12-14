package com.nb6868.onexboot.api.modules.msg.sms;

import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.SpringContextUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onexboot.api.modules.msg.entity.MailTplEntity;
import com.nb6868.onexboot.api.modules.msg.service.MailLogService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 聚合短信服务
 * see {https://www.juhe.cn/docs/api/id/54}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class JuheSmsService extends AbstractSmsService {

    public JuheSmsService() {
    }

    private static final String JUHE_SMS_SEND_URL = "http://v.juhe.cn/sms/send?key={0}&mobile={1}&tpl_id={2}&tpl_value={3}";

    @Override
    public boolean sendSms(MailTplEntity mailTpl, String phoneNumbers, String params) {
        SmsProps smsProps = JacksonUtils.jsonToPojo(mailTpl.getParam(), SmsProps.class);
        AssertUtils.isNull(smsProps, ErrorCode.PARAM_CFG_ERROR);

        MailLogService mailLogService = SpringContextUtils.getBean(MailLogService.class);
        String[] phoneNumberList = phoneNumbers.split(",", -1);
        for (String phoneNumber : phoneNumberList) {
            String url;
            StringBuilder paramJuhe = new StringBuilder();
            String content = mailTpl.getContent();
            Map<String, Object> paramJson = JacksonUtils.jsonToMap(params, new HashMap<>());
            for (String key : paramJson.keySet()) {
                // 遍历json,拼装参数
                if (StringUtils.isNotBlank(paramJuhe)) {
                    paramJuhe.append("&");
                }
                paramJuhe.append("#").append(key).append("#=").append(paramJson.get(key));
                // 拼装短信内容
                content = content.replace("#" + key + "#", paramJson.get(key).toString());
            }
            try {
                url = MessageFormat.format(JUHE_SMS_SEND_URL, smsProps.getAppKey(), phoneNumber, smsProps.getTplId(), URLEncoder.encode(paramJuhe.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException uee) {
                throw new OnexException(ErrorCode.SEND_SMS_ERROR, "组装接口地址失败");
            }
            Request request = new Request.Builder().url(url).build();

            Response response;
            try {
                response = new OkHttpClient().newCall(request).execute();
            } catch (IOException ioe) {
                throw new OnexException(ErrorCode.SEND_SMS_ERROR, "调用接口失败");
            }
            // 接口结果
            Const.ResultEnum status = response.isSuccessful() ? Const.ResultEnum.SUCCESS : Const.ResultEnum.FAIL;
            String result = "";
            if (Const.ResultEnum.SUCCESS == status) {
                try {
                    assert response.body() != null;
                    result = response.body().string();
                } catch (IOException ioe) {
                    throw new OnexException(ErrorCode.SEND_SMS_ERROR, "接口返回数据异常");
                }
                Map<String, Object> json = JacksonUtils.jsonToMap(result);
                status = (int) json.get("error_code") == 0 ? Const.ResultEnum.SUCCESS : Const.ResultEnum.FAIL;
                if (status == Const.ResultEnum.FAIL) {
                    throw new OnexException(ErrorCode.SEND_SMS_ERROR, json.get("reason").toString());
                }
            }

            // 保存记录
            MailLogEntity mailLog = new MailLogEntity();
            mailLog.setMailTo(phoneNumber);
            mailLog.setStatus(status.value());
            mailLog.setResult(result);
            mailLog.setContent(content);
            mailLog.setTplId(mailTpl.getId());
            mailLog.setTplCode(mailTpl.getCode());
            mailLog.setTplType(mailTpl.getType());
            mailLog.setContentParams(params);
            mailLog.setConsumeStatus(0);
            mailLogService.save(mailLog);
        }

        return true;
    }

}
