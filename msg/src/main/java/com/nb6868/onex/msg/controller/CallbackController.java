package com.nb6868.onex.msg.controller;

import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.pojo.IdsForm;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.msg.service.MsgLogService;
import com.nb6868.onex.msg.service.MsgService;
import com.nb6868.onex.msg.service.MsgTplService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
     * 接口不做授权检查的,所以有必要加上其他参数,比如path参数做安全校验
     */
    @PostMapping("hwcloudStatusCallback")
    @Operation(summary = "华为云短信发送状态回调")
    @AccessControl
    public Result<?> hwcloudStatusCallback(@RequestParam String smsMsgId, @RequestParam String status) {
        // todo 找到对应短信,然后更新数据
        return new Result<>();
    }

}
