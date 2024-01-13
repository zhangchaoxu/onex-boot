package com.nb6868.onex.msg.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.IdsForm;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.msg.MsgConst;
import com.nb6868.onex.msg.entity.MsgLogEntity;
import com.nb6868.onex.msg.service.MsgLogService;
import com.nb6868.onex.msg.service.MsgService;
import com.nb6868.onex.msg.service.MsgTplService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Consumer;

@RestController
@RequestMapping("/sys/msg/callback/")
@Validated
@Tag(name = "消息回调")
public class CallbackController {

    @Autowired
    MsgService msgService;
    @Autowired
    MsgTplService msgTplService;
    @Autowired
    MsgLogService msgLogService;

    /**
     * 接收状态报告API
     * see <a href="https://support.huaweicloud.com/api-msgsms/sms_05_0003.html">...</a>
     */
    @PostMapping("smsHwcloudStatusCallback")
    @Operation(summary = "华为云短信发送状态回调")
    @AccessControl
    public Result<?> smsHwcloudStatusCallback(@RequestParam String smsMsgId, @RequestParam String status, @RequestParam Long extend) {
        // 用扩展码对应数据
        MsgLogEntity msgLog = msgLogService.getById(extend);
        AssertUtils.isNull(msgLog, ErrorCode.DB_RECORD_NOT_EXISTED);
        AssertUtils.isFalse(msgLog.getState() == MsgConst.MailSendStateEnum.SUCCESS.value(), "短信状态异常");
        // 用smsMsgId做校验认证
        try {
            boolean smsMsgIdMatched = false;
            JSONObject result = JSONUtil.parseObj(msgLog.getResult());
            JSONArray resultList = result.getJSONArray("result");
            for (int i = 0; i < resultList.size(); i++) {
                if (smsMsgId.equalsIgnoreCase(resultList.getJSONObject(i).getStr("smsMsgId"))) {
                    smsMsgIdMatched = true;
                    break;
                }
            }
            if (smsMsgIdMatched) {
                // 数据校验成功,将状态结果更新到数据中
                msgLog.setResult(result.set("callbackStatus", status).toString());
                msgLogService.updateById(msgLog);
            } else {
                return new Result<>().error("smsMsgId校验失败");
            }
        } catch (Exception e) {
            return new Result<>().error("数据解析失败");
        }
        return new Result<>();
    }

    /**
     * 接收状态报告API
     * see <a href="https://help.aliyun.com/zh/sms/developer-reference/smsreport-2">...</a>
     */
    @PostMapping("smsAliyunStatusCallback")
    @Operation(summary = "阿里云短信发送状态回调")
    @AccessControl
    public Result<?> smsAliyunStatusCallback(@RequestBody JSONArray form) {
        for (int i = 0; i < form.size(); i++) {
            JSONObject itm = form.getJSONObject(i);
            String outId = itm.getStr("out_id");
            MsgLogEntity msgLog = msgLogService.getById(outId);
            if (msgLog != null && StrUtil.isNotBlank(msgLog.getResult())) {
                JSONObject result = JSONUtil.parseObj(msgLog.getResult());
                // 数据校验成功,将状态结果更新到数据中
                msgLog.setResult(result.set("callbackStatus", itm).toString());
                msgLogService.updateById(msgLog);
            }
        }
        return new Result<>();
    }

}
