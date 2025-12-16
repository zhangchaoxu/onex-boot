package com.nb6868.onex.common.sse;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.shiro.ShiroUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SseController {

    @Autowired
    SseEmitterService sseEmitterService;
    // 这里可以控制用户通知状态
    // 1:如果单用户只有一个连接(最后一个登录)可直接使用userid作为sid
    // 2:如果单用户多端登录都可连接，可使用userid-token作为sid，为什么不直接使用userid+uuid是因为不好管理
    int sseConnectType = 2;

    @GetMapping("userConnect")
    @Operation(summary = "用户连接")
    public SseEmitter userConnect(@RequestParam(required = false) String lastEventId) {
        // 重连的时候，会带上最后一次的SID
        log.info("lastEventId={}", lastEventId);
        // 获取用户id
        Long currentUserId = ShiroUtils.getUserId();
        String sid = StrUtil.format("user-{}-{}", currentUserId, IdUtil.fastSimpleUUID());
        SseEmitter sseEmitter = sseEmitterService.createSseConnect(sid);
        // 看是否只保持一个，如果只保持一个，则关闭其他
        if (sseConnectType == 1) {
            sseEmitterService.closeBySidPrefix(StrUtil.format("user-{}", currentUserId));
        }
        return sseEmitter;
    }

    @PostMapping("sendMessageByUser")
    @Operation(summary = "发送消息给指定用户,测试用")
    @RequiresPermissions(value = {"admin:super"}, logical = Logical.OR)
    public Result<?> sendMessageByUser(@Validated @RequestBody SseSendByUserReq req) {
        // 在业务逻辑中调用以下代码
        sseEmitterService.sendMessageBySidPrefix(StrUtil.format("user-{}", req.getUserId()), IdUtil.fastSimpleUUID(), req.getMessageBody());
        return new Result<>();
    }

}
