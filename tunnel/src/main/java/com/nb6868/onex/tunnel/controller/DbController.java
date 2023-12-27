package com.nb6868.onex.tunnel.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.DSFactory;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.tunnel.form.DbForm;
import com.nb6868.onex.tunnel.form.DbQueryForm;
import com.zaxxer.hikari.HikariDataSource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.List;

@RestController("TunnelDb")
@RequestMapping("/tunnel/db")
@Validated
@Slf4j
@Tag(name = "数据操作")
public class DbController {

    @PostMapping("query")
    @Operation(summary = "数据查询")
    @AccessControl(value = "/query", allowTokenName = "token-tunnel")
    public Result<?> query(@Validated @RequestBody DbQueryForm form) {
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

    @PostMapping("execute")
    @Operation(summary = "数据执行")
    @AccessControl(value = "/execute", allowTokenName = "token-tunnel")
    public Result<?> execute(@Validated @RequestBody DbQueryForm form) {
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

}
