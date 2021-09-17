package com.nb6868.onex.common.config;

import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Collections;
import java.util.List;

/**
 * Swagger配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    @Value("${knife4j.title:Govsz-Plus-API}")
    private String title;
    @Value("${knife4j.description:API}")
    private String description;
    @Value("${knife4j.version:1.0.0}")
    private String version;
    @Value("${onex.login.auth-token-key:auth-token}")
    private String authTokenKey;

    private final OpenApiExtensionResolver openApiExtensionResolver;

    @Autowired
    public SwaggerConfig(OpenApiExtensionResolver openApiExtensionResolver) {
        this.openApiExtensionResolver = openApiExtensionResolver;
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 包下的类，生成接口文档
                //.apis(RequestHandlerSelectors.basePackage("com.nb6868.onex.modules.*.controller"))
                .paths(PathSelectors.any())
                .build()
                .extensions(openApiExtensionResolver.buildSettingExtensions())
                .directModelSubstitute(java.util.Date.class, String.class)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version)
                .build();
    }

    private List<SecurityScheme> securitySchemes() {
        return Collections.singletonList(new ApiKey(authTokenKey, authTokenKey, io.swagger.models.auth.In.HEADER.toValue()));
    }

    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder().securityReferences(Collections.singletonList(
                        new SecurityReference(authTokenKey, new AuthorizationScope[]{ new AuthorizationScope("global", "") }))
                ).build()
        );
    }

}
