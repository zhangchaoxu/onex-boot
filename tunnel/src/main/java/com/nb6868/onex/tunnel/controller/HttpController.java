package com.nb6868.onex.tunnel.controller;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.tunnel.form.DbForm;
import com.nb6868.onex.tunnel.form.DbQueryForm;
import com.nb6868.onex.tunnel.form.HttpQueryForm;
import com.zaxxer.hikari.HikariDataSource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.List;

@RestController("TunnelHttp")
@RequestMapping("/tunnel/http")
@Validated
@Slf4j
@Tag(name = "网络请求操作")
public class HttpController {

    @PostMapping("execute")
    @Operation(summary = "接口调用")
    @AccessControl(value = "/execute", allowTokenName = "token-tunnel")
    public Result<?> execute(@Validated @RequestBody HttpQueryForm form) {
        try {
            String result = HttpRequest.of(form.getUrl())
                    .headerMap(form.getHeaders(), true)
                    .body(form.getBody())
                    .form(form.getParams())
                    .method(Method.valueOf(form.getMethod()))
                    .timeout(form.getTimeout() <= 0 ? (int) DateUnit.MINUTE.getMillis() : form.getTimeout())
                    .execute().body();
            return new Result<>().success(result);
        } catch (Exception e) {
            log.error("数据请求失败", e);
            return new Result<>().error(e.getMessage());
        }
    }

}
