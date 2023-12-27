package com.nb6868.onex.tunnel.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.system.SystemUtil;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.pojo.form.RuntimeExecCmdQuery;
import com.nb6868.onex.tunnel.form.SystemQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController("TunnelSystem")
@RequestMapping("/tunnel/system")
@Validated
@Slf4j
@Tag(name = "系统信息")
public class SystemController {

    @PostMapping("systemInfo")
    @Operation(summary = "系统信息")
    @AccessControl(value = "/systemInfo", allowTokenName = "token-tunnel")
    public Result<?> systemInfo(@Validated @RequestBody BaseForm form) {
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


    @PostMapping("systemProp")
    @Operation(summary = "系统参数属性")
    @AccessControl(value = "/systemProp", allowTokenName = "token-tunnel")
    public Result<?> systemProp(@Validated @RequestBody SystemQuery form) {
        try {
            String propValue = SystemUtil.get(form.getName(), form.isQuiet());
            Dict result = Dict.create().set("quite", form.isQuiet()).set("name", form.getName()).set("value", propValue);
            return new Result<>().success(result);
        } catch (Exception e) {
            log.error("读取参数[" + form.getName() + "]失败", e);
            return new Result<>().error(e.getMessage());
        }
    }

    @PostMapping("runtime")
    @Operation(summary = "命令行")
    @AccessControl(value = "/runtime", allowTokenName = "token-tunnel")
    public Result<?> runtime(@Validated @RequestBody RuntimeExecCmdQuery form) {
        try {
            String runResult = RuntimeUtil.execForStr(form.getCmd());
            Dict result = Dict.create().set("cmd", form.getCmd()).set("runResult", runResult);
            return new Result<>().success(result);
        } catch (Exception e) {
            log.error("执行命令[" + form.getCmd() + "]失败", e);
            return new Result<>().error(e.getMessage());
        }
    }
}
