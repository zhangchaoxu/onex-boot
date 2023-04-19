package com.nb6868.onex.common.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

/**
 * 基础swagger config
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public abstract class BaseSwaggerConfig {

    @Value("${knife4j.title:OneX-API}")
    private String title;
    @Value("${knife4j.description:API}")
    private String description;
    @Value("${knife4j.version:1.0.0}")
    private String version;
    @Value("${onex.auth.token-header-key:auth-token}")
    private String authTokenKey;

    @Bean("portal")
    @ConditionalOnProperty(name = "onex.swagger.portal", havingValue = "true")
    public Docket createBizApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfoBuilder().build())
                .groupName("portal-业务端")
                .select()
                // 包下的类,生成接口文档
                .apis(RequestHandlerSelectors.basePackage("com.nb6868.onex.portal.modules"))
                .paths(PathSelectors.any())
                .build()
                .extensions(getExtensions())
                .directModelSubstitute(java.util.Date.class, String.class)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    @Bean("client")
    @ConditionalOnProperty(name = "onex.swagger.client", havingValue = "true")
    public Docket createClientApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfoBuilder().build())
                .groupName("client-客户端")
                .select()
                // 包下的类,生成接口文档
                .apis(RequestHandlerSelectors.basePackage("com.nb6868.onex.client"))
                .paths(PathSelectors.any())
                .build()
                .extensions(getExtensions())
                .directModelSubstitute(java.util.Date.class, String.class)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    @Bean("uc")
    @ConditionalOnProperty(name = "onex.swagger.uc", havingValue = "true")
    public Docket createUcApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfoBuilder().build())
                .groupName("uc-用户中心")
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

    @Bean("sys")
    @ConditionalOnProperty(name = "onex.swagger.sys", havingValue = "true")
    public Docket createSysApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfoBuilder().build())
                .groupName("sys-系统管理")
                .select()
                // 包下的类,生成接口文档
                .apis(RequestHandlerSelectors.basePackage("com.nb6868.onex.sys.controller")
                        .or(RequestHandlerSelectors.basePackage("com.nb6868.onex.websocket.controller"))
                        .or(RequestHandlerSelectors.basePackage("com.nb6868.onex.msg.controller"))
                        .or(RequestHandlerSelectors.basePackage("com.nb6868.onex.job.controller"))
                )
                .paths(PathSelectors.any())
                .build()
                .extensions(getExtensions())
                .directModelSubstitute(java.util.Date.class, String.class)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    @Bean("default")
    @ConditionalOnProperty(name = "onex.swagger.default", havingValue = "true")
    public Docket createDefaultApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfoBuilder().build())
                .groupName("default")
                .select()
                // 扫描注解,生成接口文档
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 包下的类,生成接口文档
                //.apis(RequestHandlerSelectors.basePackage("com.nb6868.onex.modules.*.controller"))
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
                        new SecurityReference(authTokenKey, new AuthorizationScope[]{new AuthorizationScope("global", "")}))
                ).build()
        );
    }
}
