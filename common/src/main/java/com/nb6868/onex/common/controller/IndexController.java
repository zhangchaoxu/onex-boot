package com.nb6868.onex.common.controller;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.pojo.BaseReq;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.util.IpRegionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Validated
@Tag(name = "首页")
public class IndexController {

    @GetMapping("/")
    @Operation(summary = "index")
    @AccessControl("")
    public Result<?> index() {
        JSONObject result = new JSONObject()
                .set("tips", "hello onex")
                .set("onex", new JSONObject()
                        .set("parentArtifactId", SpringUtil.getProperty("onex.parent-artifact-id"))
                        .set("artifactId", SpringUtil.getProperty("onex.artifact-id"))
                        .set("version", SpringUtil.getProperty("onex.version"))
                        .set("buildTime", SpringUtil.getProperty("onex.build-time")))
                .set("app", new JSONObject()
                        .set("parentArtifactId", SpringUtil.getProperty("onex.app.parent-artifact-id"))
                        .set("artifactId", SpringUtil.getProperty("onex.app.artifact-id"))
                        .set("version", SpringUtil.getProperty("onex.app.version"))
                        .set("buildTime", SpringUtil.getProperty("onex.app.build-time")));
        return new Result<>().success(result);
    }

    @GetMapping("clientInfo")
    @Operation(summary = "客户端信息")
    public Result<?> clientInfo(HttpServletRequest request) {
        String ua = request.getHeader(HttpHeaders.USER_AGENT);
        String ip = HttpContextUtils.getIpAddr(request);
        JSONObject result = new JSONObject()
                .set("requestUA", ua)
                .set("userAgent", UserAgentUtil.parse(ua))
                .set("requestIp", ip)
                .set("requestIpRegion", IpRegionUtil.getRegion(ip));
        return new Result<>().success(result);
    }

}
