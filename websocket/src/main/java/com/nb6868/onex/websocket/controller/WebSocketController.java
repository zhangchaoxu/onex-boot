package com.nb6868.onex.websocket.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.websocket.dto.WebSocketSendForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("WebSocketController")
@RequestMapping("/webSocket")
@Validated
@Api(tags = "WebSocket")
public class WebSocketController {

    @Autowired
    private WebSocketServer webSocketServer;

    @PostMapping("getOpenSockets")
    @ApiOperation("获得目前连接的Socket")
    @RequiresPermissions("websocket:query")
    @ApiOperationSupport(order = 10)
    public Result<?> getOpenSockets() {
        List<String> sidList = webSocketServer.getSidList();
        return new Result<>().success(sidList);
    }

    @PostMapping("sendOneMessage")
    @ApiOperation("发送单点消息")
    @RequiresPermissions("websocket:send")
    @ApiOperationSupport(order = 20)
    public Result<?> sendOneMessage(@Validated(value = {DefaultGroup.class, WebSocketSendForm.SendOneGroup.class}) @RequestBody WebSocketSendForm form) {
        boolean result = webSocketServer.sendOneMessage(form.getSid(), form.getContent());
        return new Result<>().boolResult(result);
    }

    @PostMapping("sendMultiMessage")
    @ApiOperation("发送批量消息")
    @RequiresPermissions("websocket:send")
    @ApiOperationSupport(order = 30)
    public Result<?> sendMultiMessage(@Validated(value = {DefaultGroup.class, WebSocketSendForm.SendMultiGroup.class}) @RequestBody WebSocketSendForm form) {
        webSocketServer.sendMultiMessage(form.getSidList(), form.getContent());
        return new Result<>();
    }

    @PostMapping("sendAllMessage")
    @ApiOperation("发送广播消息")
    @RequiresPermissions("websocket:send")
    @ApiOperationSupport(order = 40)
    public Result<?> sendAllMessage(@Validated(value = {DefaultGroup.class}) @RequestBody WebSocketSendForm socketSendRequest) {
        webSocketServer.sendAllMessage(socketSendRequest.getContent());
        return new Result<>();
    }

}
