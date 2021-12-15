package com.nb6868.onex.common.websocket;

import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
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

    @GetMapping("getOpenSockets")
    @ApiOperation("获得目前连接的Socket")
    @RequiresRoles("SuperAdmin")
    public Result<?> getOpenSockets() {
        List<String> sidList = webSocketServer.getSidList();
        return new Result<>().success(sidList);
    }

    @PostMapping("sendMultiMessage")
    @ApiOperation("发送批量消息")
    @RequiresRoles("SuperAdmin")
    public Result<?> sendMultiMessage(@Validated(value = {DefaultGroup.class, WebSocketSendForm.SendMultiGroup.class}) @RequestBody WebSocketSendForm form) {
        webSocketServer.sendMultiMessage(form.getSidList(), form.getContent());
        return new Result<>();
    }

    @PostMapping("sendOneMessage")
    @ApiOperation("发送单点消息")
    @RequiresRoles("SuperAdmin")
    public Result<?> sendOneMessage(@Validated(value = {DefaultGroup.class, WebSocketSendForm.SendOneGroup.class}) @RequestBody WebSocketSendForm form) {
        boolean result = webSocketServer.sendOneMessage(form.getSid(), form.getContent());
        if (result) {
            return new Result<>();
        } else {
            return new Result<>().error("发送失败");
        }
    }

    @PostMapping("sendAllMessage")
    @ApiOperation("发送广播消息")
    @RequiresRoles("SuperAdmin")
    public Result<?> sendAllMessage(@Validated(value = {DefaultGroup.class}) @RequestBody WebSocketSendForm socketSendRequest) {
        webSocketServer.sendAllMessage(socketSendRequest.getContent());
        return new Result<>();
    }

}
