package com.nb6868.onexboot.api.common.config;

import com.nb6868.onexboot.api.modules.uc.UcConst;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Swagger配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 加了ApiOperation注解的类，生成接口文档
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 包下的类，生成接口文档
                // .apis(RequestHandlerSelectors.basePackage("com.nb6868.onexboot.api.modules.*.controller"))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(java.util.Date.class, String.class)
                .securitySchemes(security());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API")
                .description("接口文档")
                .version("1.0.0")
                .build();
    }

    private List<ApiKey> security() {
        return newArrayList(new ApiKey(UcConst.TOKEN_HEADER, UcConst.TOKEN_HEADER, "header"));
    }
}
