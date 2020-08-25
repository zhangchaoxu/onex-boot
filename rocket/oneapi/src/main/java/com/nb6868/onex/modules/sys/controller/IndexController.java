package com.nb6868.onex.modules.sys.controller;

import com.nb6868.onex.booster.pojo.Kv;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.util.DateUtils;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.modules.uc.user.SecurityUser;
import com.nb6868.onex.modules.uc.user.UserDetail;
import com.sun.management.OperatingSystemMXBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 系统接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@Validated
@Api(tags = "首页接口")
public class IndexController {

    @GetMapping("/")
    @ApiOperation("系统信息")
    @AccessControl(anon = true)
    public Result<?> index() {
        UserDetail userDetail = SecurityUser.getUser();
        return new Result<>().success("api success");
    }

    @GetMapping("sys/info")
    @ApiOperation("系统信息")
    public Result<?> sysInfo() {
        OperatingSystemMXBean osmx = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        Kv data = Kv.init();
        data.set("sysTime", DateUtils.now());
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
