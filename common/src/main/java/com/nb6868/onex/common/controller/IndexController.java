package com.nb6868.onex.common.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.pojo.Result;
import com.sun.management.OperatingSystemMXBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequestMapping("/")
@Validated
@Api(tags = "首页")
public class IndexController {

    @GetMapping("/")
    @ApiOperation("index")
    @AccessControl("")
    public Result<?> index() {
        JSONObject result = new JSONObject()
                .set("onex", new JSONObject()
                        .set("parent-artifact-id", SpringUtil.getProperty("onex.parent-artifact-id"))
                        .set("artifact-id", SpringUtil.getProperty("onex.artifact-id"))
                        .set("version", SpringUtil.getProperty("onex.version"))
                        .set("build-time", SpringUtil.getProperty("onex.build-time")))
                .set("app", new JSONObject()
                        .set("parent-artifact-id", SpringUtil.getProperty("onex.app.parent-artifact-id"))
                        .set("artifact-id", SpringUtil.getProperty("onex.app.artifact-id"))
                        .set("version", SpringUtil.getProperty("onex.app.version"))
                        .set("build-time", SpringUtil.getProperty("onex.app.build-time")));
        return new Result<>().success(result);
    }

    @GetMapping("sysInfo")
    @ApiOperation("系统信息")
    @AccessControl("sysInfo")
    @SuppressWarnings("deprecation")
    public Result<?> sysInfo() {
        OperatingSystemMXBean osmx = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        JSONObject result = new JSONObject()
                .set("sysTime", DateUtil.now())
                .set("osName", System.getProperty("os.name"))
                .set("osArch", System.getProperty("os.arch"))
                .set("osVersion", System.getProperty("os.version"))
                .set("userLanguage", System.getProperty("user.language"))
                .set("userDir", System.getProperty("user.dir"))
                .set("totalPhysical", osmx.getTotalPhysicalMemorySize() / 1024 / 1024)
                .set("freePhysical", osmx.getFreePhysicalMemorySize() / 1024 / 1024)
                .set("memoryRate", BigDecimal.valueOf((1 - osmx.getFreePhysicalMemorySize() * 1.0 / osmx.getTotalPhysicalMemorySize()) * 100).setScale(2, RoundingMode.HALF_UP))
                .set("processors", osmx.getAvailableProcessors())
                .set("jvmName", System.getProperty("java.vm.name"))
                .set("javaVersion", System.getProperty("java.version"))
                .set("javaHome", System.getProperty("java.home"))
                .set("javaTotalMemory", Runtime.getRuntime().totalMemory() / 1024 / 1024)
                .set("javaFreeMemory", Runtime.getRuntime().freeMemory() / 1024 / 1024)
                .set("javaMaxMemory", Runtime.getRuntime().maxMemory() / 1024 / 1024)
                .set("userName", System.getProperty("user.name"))
                .set("systemCpuLoad", BigDecimal.valueOf(osmx.getSystemCpuLoad() * 100).setScale(2, RoundingMode.HALF_UP))
                .set("userTimezone", System.getProperty("user.timezone"));

        return new Result<>().success(result);
    }
}
