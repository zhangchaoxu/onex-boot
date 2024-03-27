package com.nb6868.onex.common.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.pojo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
        Dict result = Dict.create()
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

}
