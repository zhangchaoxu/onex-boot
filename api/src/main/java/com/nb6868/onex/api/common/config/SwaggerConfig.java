package com.nb6868.onex.api.common.config;

import com.nb6868.onex.api.modules.uc.UcConst;
import io.swagger.annotations.ApiOperation;
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

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("1.0.0版本")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 包下的类，生成接口文档
                //.apis(RequestHandlerSelectors.basePackage("com.nb6868.onexboot.api.modules.*.controller"))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(java.util.Date.class, String.class)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("OneX API")
                .description("接口文档")
                .version("1.0.0")
                .build();
    }

    private List<SecurityScheme> securitySchemes() {
        return Collections.singletonList(new ApiKey(UcConst.TOKEN_HEADER, UcConst.TOKEN_HEADER, io.swagger.models.auth.In.HEADER.toValue()));
    }

    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder().securityReferences(Collections.singletonList(
                        new SecurityReference(UcConst.TOKEN_HEADER, new AuthorizationScope[]{ new AuthorizationScope("global", "") }))
                ).build()
        );
    }

}
