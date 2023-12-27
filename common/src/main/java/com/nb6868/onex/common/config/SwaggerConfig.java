package com.nb6868.onex.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
@ConditionalOnProperty(name = "onex.swagger.enable", havingValue = "true")
public class SwaggerConfig {

    @Value("${knife4j.title:OneX-API}")
    private String title;
    @Value("${knife4j.description:API}")
    private String description;
    @Value("${knife4j.version:1.0.0}")
    private String version;
    @Value("${onex.auth.access-control.token-header-key:auth-token}")
    private String authTokenKey;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 设置openapi基本信息
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description))
                // 添加全局auth-token的头
                .components(new Components().addSecuritySchemes(authTokenKey,
                        new SecurityScheme()
                                .name(authTokenKey)
                                .in(SecurityScheme.In.HEADER)
                                .type(SecurityScheme.Type.HTTP)));
    }

    /**
     * 自定义openApi
     */
    @Bean
    public GlobalOpenApiCustomizer customGlobalOpenApiCustomizer() {
        return openApi -> {
            // 对tag做处理,影响controller
            if (openApi.getTags() != null) {
                openApi.getTags().forEach(tag -> {
                    // 对tags做排序,需要设置tags-sorter: order
                    // tag.setExtensions(Map.of("x-order", tag.getDescription().charAt(0)));
                });
            }
            // 对operations做处理,影响controller中的方法
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((s, pathItem) -> pathItem.readOperations().forEach(operation -> {
                    // 解决文档中请求头部不显示的问题
                    // 可以在controller中加@SecurityRequirement(name ="auth-token")
                    // 也可以在method中@Operation(summary = "分页", security = {@SecurityRequirement(name = "auth-token")})
                    // 尝试在这里批量在Operation中添加SecurityRequirement
                    operation.addSecurityItem(new SecurityRequirement().addList(authTokenKey));
                    // 解决排序问题，需要设置operations-sorter: order
                    // operation.setExtensions(Map.of("x-order", operation.getDescription().charAt(0)));
                }));
            }
        };

    }

}
