package com.nb6868.onex.common.sse;

import cn.hutool.json.JSONUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController("SysSseController")
@RequestMapping("/sys/sse/")
@Validated
@Tag(name = "SSE")
public class SseController {

    @Autowired
    private SseEmitterService sseEmitterService;

    @GetMapping("connect")
    @AccessControl
    @Operation(summary = "创建连接")
    public SseEmitter connect(@RequestParam String sid) {
        return sseEmitterService.createSseConnect(sid);
    }

    @PostMapping("sendOneMessage")
    @Operation(summary = "发送单点消息")
    @RequiresPermissions(value = {"admin:super", "admin:sse", "sys:sse:send"}, logical = Logical.OR)
    @ApiOperationSupport(order = 20)
    public Result<?> sendOneMessage(@Validated(value = {DefaultGroup.class, SseSendForm.SendOneGroup.class}) @RequestBody SseSendForm form) {
        sseEmitterService.sendOneMessage(form.getSid(), JSONUtil.parseObj(form.getContent()));
        return new Result<>();
    }

}
