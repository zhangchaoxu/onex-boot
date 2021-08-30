package com.nb6868.onex.api.modules.sys.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.AccessLimit;
import com.nb6868.onex.common.pojo.Result;
import com.sun.management.OperatingSystemMXBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

/**
 * 系统接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/")
@Validated
@Api(tags = "首页")
@Slf4j
public class IndexController {

    @GetMapping("/")
    @ApiOperation("首页")
    @AccessControl
    public Result<?> index() {
        log.info("index log");
        return new Result<>().success("api success");
    }

    @GetMapping("/limitTest")
    @ApiOperation("限流测试")
    @AccessControl
    @AccessLimit
    public Result<?> limitTest() throws Exception {
        log.info("limitTest log");
        TimeUnit.SECONDS.sleep(1);
        return new Result<>().success("success");
    }

    @GetMapping("sys/info")
    @ApiOperation("系统信息")
    public Result<?> sysInfo() {
        OperatingSystemMXBean osmx = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        Dict data = Dict.create();
        data.set("sysTime", DateUtil.now());
        data.set("osName", System.getProperty("os.name"));
        data.set("osArch", System.getProperty("os.arch"));
        data.set("osVersion", System.getProperty("os.version"));
        data.set("userLanguage", System.getProperty("user.language"));
        data.set("userDir", System.getProperty("user.dir"));
        data.set("totalPhysical", osmx.getTotalPhysicalMemorySize() / 1024 / 1024);
        data.set("freePhysical", osmx.getFreePhysicalMemorySize() / 1024 / 1024);

        data.set("memoryRate", BigDecimal.valueOf((1 - osmx.getFreePhysicalMemorySize() * 1.0 / osmx.getTotalPhysicalMemorySize()) * 100).setScale(2, RoundingMode.HALF_UP));
        data.set("processors", osmx.getAvailableProcessors());
        data.set("jvmName", System.getProperty("java.vm.name"));
        data.set("javaVersion", System.getProperty("java.version"));
        data.set("javaHome", System.getProperty("java.home"));
        data.set("javaTotalMemory", Runtime.getRuntime().totalMemory() / 1024 / 1024);
        data.set("javaFreeMemory", Runtime.getRuntime().freeMemory() / 1024 / 1024);
        data.set("javaMaxMemory", Runtime.getRuntime().maxMemory() / 1024 / 1024);
        data.set("userName", System.getProperty("user.name"));
        data.set("systemCpuLoad", BigDecimal.valueOf(osmx.getSystemCpuLoad() * 100).setScale(2, RoundingMode.HALF_UP));
        data.set("userTimezone", System.getProperty("user.timezone"));

        return new Result<>().success(data);
    }

}
