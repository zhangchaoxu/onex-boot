package com.nb6868.onexboot.api.modules.sys.controller;

import com.nb6868.onexboot.common.pojo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * WebSocket
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("webSocket")
@Validated
@Api(tags="WebSocket")
public class WebSocketController {

    @Autowired
    private WebSocketServer webSocketServer;

    @GetMapping("getOpenSockets")
    @ApiOperation("获得目前连接的Socket")
    public Result<?> getOpenSockets(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<>();
    }

    @PostMapping("sendOneMessage")
    @ApiOperation("发送单点消息")
    public Result<?> sendOneMessage(@RequestParam Long id, @RequestParam String content) {
        webSocketServer.sendOneMessage(id, content);
        return new Result<>();
    }
}
