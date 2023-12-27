package com.nb6868.onex.common.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.system.SystemUtil;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.pojo.form.DbForm;
import com.nb6868.onex.common.pojo.form.DbQueryForm;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.pojo.form.RuntimeExecCmdQuery;
import com.nb6868.onex.common.pojo.form.SystemQuery;
import com.nb6868.onex.common.validator.AssertUtils;
import com.zaxxer.hikari.HikariDataSource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController("Tunnel")
@RequestMapping("/tunnel")
@Validated
@Slf4j
@Tag(name = "隧道")
public class TunnelController {

    @PostMapping("dbQuery")
    @Operation(summary = "数据查询")
    @AccessControl(value = "/dbQuery", allowTokenName = "token-tunnel")
    public Result<?> dbQuery(@Validated @RequestBody DbQueryForm form) {
        // init 数据源
        DataSource ds = initDataSource(form);
        // 执行数据查询
        try {
            List<Entity> result = Db.use(ds).query(form.getSql(), form.getParams());
            return new Result<>().success(result);
        } catch (Exception e) {
            return new Result<>().error(e.getMessage());
        }
    }

    @PostMapping("dbExecute")
    @Operation(summary = "数据执行")
    @AccessControl(value = "/dbExecute", allowTokenName = "token-tunnel")
    public Result<?> dbExecute(@Validated @RequestBody DbQueryForm form) {
        // init 数据源
        DataSource ds = initDataSource(form);
        // 执行数据查询
        try {
            int result = Db.use(ds).execute(form.getSql(), form.getParams());
            return new Result<>().success(result);
        } catch (Exception e) {
            return new Result<>().error(e.getMessage());
        }
    }

    /**
     * 初始化数据源
     */
    private DataSource initDataSource(DbForm form) {
        // 检查输入
        AssertUtils.isTrue((StrUtil.isEmpty(form.getDs()) && StrUtil.hasBlank(form.getUrl(), form.getUsername(), form.getPassword())), "请指定数据源");
        try {
            if (StrUtil.isNotBlank(form.getDs())) {
                // 指定ds
                return DSFactory.get(form.getDs());
            } else {
                // 未指定ds
                HikariDataSource ds = new HikariDataSource();
                ds.setJdbcUrl(form.getUrl());
                ds.setUsername(form.getUsername());
                ds.setPassword(form.getPassword());
                return ds;
            }
        } catch (Exception e) {
            log.error("数据库初始化失败", e);
            throw new OnexException("数据源初始化失败");
        }
    }

    @PostMapping("system")
    @Operation(summary = "系统属性")
    @AccessControl(value = "/system", allowTokenName = "token-tunnel")
    public Result<?> system(@Validated @RequestBody SystemQuery form) {
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

}
