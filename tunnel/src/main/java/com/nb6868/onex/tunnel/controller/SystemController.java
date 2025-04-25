package com.nb6868.onex.tunnel.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.system.SystemUtil;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.pojo.BaseReq;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.tunnel.dto.RuntimeExecCmdReq;
import com.nb6868.onex.tunnel.dto.SystemPropReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("TunnelSystem")
@RequestMapping("/tunnel/system/")
@Validated
@Slf4j
@Tag(name = "系统信息")
public class SystemController {

    @PostMapping("info")
    @Operation(summary = "系统信息")
    @AccessControl(value = "info", allowTokenName = "token-tunnel")
    public Result<?> info(@Validated @RequestBody BaseReq req) {
        Dict result = Dict.create()
                .set("sysTime", DateUtil.now())
                .set("currentPID", SystemUtil.getCurrentPID())
                .set("osName", System.getProperty("os.name"))
                .set("osArch", System.getProperty("os.arch"))
                .set("osVersion", System.getProperty("os.version"))
                .set("userLanguage", System.getProperty("user.language"))
                .set("userDir", System.getProperty("user.dir"))
                .set("jvmName", System.getProperty("java.vm.name"))
                .set("javaVersion", System.getProperty("java.version"))
                .set("javaHome", System.getProperty("java.home"))
                .set("javaTotalMemory", SystemUtil.getTotalMemory() / 1024 / 1024)
                .set("javaFreeMemory", SystemUtil.getFreeMemory() / 1024 / 1024)
                .set("javaMaxMemory", SystemUtil.getMaxMemory() / 1024 / 1024)
                .set("userName", System.getProperty("user.name"))
                .set("userTimezone", System.getProperty("user.timezone"));
        return new Result<>().success(result);
    }

    @PostMapping("prop")
    @Operation(summary = "系统参数属性")
    @AccessControl(value = "prop", allowTokenName = "token-tunnel")
    public Result<?> prop(@Validated @RequestBody SystemPropReq req) {
        try {
            String propValue = SystemUtil.get(req.getName(), req.isQuiet());
            Dict result = Dict.create().set("quite", req.isQuiet()).set("name", req.getName()).set("value", propValue);
            return new Result<>().success(result);
        } catch (Exception e) {
            log.error("读取参数[{}]失败", req.getName(), e);
            return new Result<>().error(e.getMessage());
        }
    }

    @PostMapping("runtime")
    @Operation(summary = "命令行")
    @AccessControl(value = "runtime", allowTokenName = "token-tunnel")
    public Result<?> runtime(@Validated @RequestBody RuntimeExecCmdReq req) {
        try {
            String runResult = RuntimeUtil.execForStr(req.getCmd());
            Dict result = Dict.create().set("cmd", req.getCmd()).set("runResult", runResult);
            return new Result<>().success(result);
        } catch (Exception e) {
            log.error("执行命令[{}]失败", req.getCmd(), e);
            return new Result<>().error(e.getMessage());
        }
    }
}
