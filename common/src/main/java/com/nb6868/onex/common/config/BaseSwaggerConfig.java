package com.nb6868.onex.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseSwaggerConfig {

    @Value("${knife4j.title:OneX-API}")
    private String title;
    @Value("${knife4j.description:API}")
    private String description;
    @Value("${knife4j.version:1.0.0}")
    private String version;
    @Value("${onex.auth.token-key:auth-token}")
    private String authTokenKey;

    @Bean("sched")
    public Docket createSchedApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfoBuilder().build())
                .groupName("sched")
                .select()
                // 包下的类,生成接口文档
                .apis(RequestHandlerSelectors.basePackage("com.nb6868.onex.sched.controller"))
                .paths(PathSelectors.any())
                .build()
                .extensions(getExtensions())
                .directModelSubstitute(java.util.Date.class, String.class)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    @Bean("websocket")
    public Docket createWebsocketApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfoBuilder().build())
                .groupName("websocket")
                .select()
                // 包下的类,生成接口文档
                .apis(RequestHandlerSelectors.basePackage("com.nb6868.onex.websocket.controller"))
                .paths(PathSelectors.any())
                .build()
                .extensions(getExtensions())
                .directModelSubstitute(java.util.Date.class, String.class)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    @Bean("msg")
    public Docket createMsgApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfoBuilder().build())
                .groupName("msg")
                .select()
                // 包下的类,生成接口文档
                .apis(RequestHandlerSelectors.basePackage("com.nb6868.onex.msg.controller"))
                .paths(PathSelectors.any())
                .build()
                .extensions(getExtensions())
                .directModelSubstitute(java.util.Date.class, String.class)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    @Bean("cms")
    public Docket createCmsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfoBuilder().build())
                .groupName("cms")
                .select()
                // 包下的类,生成接口文档
                .apis(RequestHandlerSelectors.basePackage("com.nb6868.onex.cms.controller"))
                .paths(PathSelectors.any())
                .build()
                .extensions(getExtensions())
                .directModelSubstitute(java.util.Date.class, String.class)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    @Bean("uc")
    public Docket createUcApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfoBuilder().build())
                .groupName("uc")
                .select()
                // 包下的类,生成接口文档
                .apis(RequestHandlerSelectors.basePackage("com.nb6868.onex.uc.controller"))
                .paths(PathSelectors.any())
                .build()
                .extensions(getExtensions())
                .directModelSubstitute(java.util.Date.class, String.class)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    protected List<VendorExtension> getExtensions() {
        return new ArrayList<>();
    }

    protected ApiInfoBuilder apiInfoBuilder() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version);
    }

    protected List<SecurityScheme> securitySchemes() {
        return Collections.singletonList(new ApiKey(authTokenKey, authTokenKey, io.swagger.models.auth.In.HEADER.toValue()));
    }

    protected List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder().securityReferences(Collections.singletonList(
                        new SecurityReference(authTokenKey, new AuthorizationScope[]{ new AuthorizationScope("global", "") }))
                ).build()
        );
    }
}
