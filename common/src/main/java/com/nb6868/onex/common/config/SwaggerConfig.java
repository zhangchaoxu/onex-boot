package com.nb6868.onex.common.config;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.github.xiaoymin.knife4j.core.conf.ExtensionsConstants;
import com.github.xiaoymin.knife4j.core.conf.GlobalConstants;
import com.github.xiaoymin.knife4j.spring.configuration.Knife4jProperties;
import com.github.xiaoymin.knife4j.spring.configuration.Knife4jSetting;
import com.github.xiaoymin.knife4j.spring.extension.Knife4jOpenApiCustomizer;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Swagger配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
@ConditionalOnProperty(name = "onex.swagger.enable", havingValue = "true")
@Slf4j
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
                    // 默认用方法名作为operationId，存在同名问题
                    operation.setOperationId(operation.getTags().get(0) + "-" + operation.getOperationId());
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

    @PostConstruct
    public void init() {
        log.info("init swaggerConfig");
    }

    // 以下这段代码是为了解决springboot3+中使用knife4j中出现 java.lang.NoSuchMethodError: 'java.util.List org.springdoc.core.properties.SpringDocConfigProperties.getGroupConfigs()'
    // 根本原因是SpringDocConfigProperties.getGroupConfigs()的返回类型发生了变化从list变成了set
    // 很巧妙的在于set和list的处理方式一样，所以只需要重新定义一遍Knife4jOpenApiCustomizer或者同包类重新写一遍即可
    // 更多讨论见https://github.com/xiaoymin/knife4j/issues/865
    @Bean
    public Knife4jOpenApiCustomizer knife4jOpenApiCustomizer(Knife4jProperties knife4jProperties, SpringDocConfigProperties docProperties) {
        return new Knife4jOpenApiCustomizer(knife4jProperties, docProperties) {
            @Override
            public void customise(OpenAPI openApi) {
                log.debug("MyKnife4jOpenApiCustomizer Knife4j OpenApiCustomizer");
                if (knife4jProperties.isEnable()) {
                    Knife4jSetting setting = knife4jProperties.getSetting();
                    OpenApiExtensionResolver openApiExtensionResolver = new OpenApiExtensionResolver(setting, knife4jProperties.getDocuments());
                    // 解析初始化
                    openApiExtensionResolver.start();
                    Map<String, Object> objectMap = new HashMap<>();
                    objectMap.put(GlobalConstants.EXTENSION_OPEN_SETTING_NAME, setting);
                    objectMap.put(GlobalConstants.EXTENSION_OPEN_MARKDOWN_NAME, openApiExtensionResolver.getMarkdownFiles());
                    openApi.addExtension(GlobalConstants.EXTENSION_OPEN_API_NAME, objectMap);
                    addOrderExtension(openApi);
                }
            }

            private void addOrderExtension(OpenAPI openApi) {
                if (CollectionUtils.isEmpty(docProperties.getGroupConfigs())) {
                    return;
                }
                // 获取包扫描路径
                Set<String> packagesToScan =
                        docProperties.getGroupConfigs().stream()
                                .map(SpringDocConfigProperties.GroupConfig::getPackagesToScan)
                                .filter(toScan -> !CollectionUtils.isEmpty(toScan))
                                .flatMap(List::stream)
                                .collect(Collectors.toSet());
                if (CollectionUtils.isEmpty(packagesToScan)) {
                    return;
                }
                // 扫描包下被ApiSupport注解的RestController Class
                Set<Class<?>> classes =
                        packagesToScan.stream()
                                .map(packageToScan -> scanPackageByAnnotation(packageToScan, RestController.class))
                                .flatMap(Set::stream)
                                .filter(clazz -> clazz.isAnnotationPresent(ApiSupport.class))
                                .collect(Collectors.toSet());
                if (!CollectionUtils.isEmpty(classes)) {
                    // ApiSupport oder值存入tagSortMap<Tag.name,ApiSupport.order>
                    Map<String, Integer> tagOrderMap = new HashMap<>();
                    classes.forEach(
                            clazz -> {
                                Tag tag = getTag(clazz);
                                if (Objects.nonNull(tag)) {
                                    ApiSupport apiSupport = clazz.getAnnotation(ApiSupport.class);
                                    tagOrderMap.putIfAbsent(tag.name(), apiSupport.order());
                                }
                            });
                    // 往openApi tags字段添加x-order增强属性
                    if (openApi.getTags() != null) {
                        openApi
                                .getTags()
                                .forEach(
                                        tag -> {
                                            if (tagOrderMap.containsKey(tag.getName())) {
                                                tag.addExtension(
                                                        ExtensionsConstants.EXTENSION_ORDER, tagOrderMap.get(tag.getName()));
                                            }
                                        });
                    }
                }
            }

            private Tag getTag(Class<?> clazz) {
                // 从类上获取
                Tag tag = clazz.getAnnotation(Tag.class);
                if (Objects.isNull(tag)) {
                    // 从接口上获取
                    Class<?>[] interfaces = clazz.getInterfaces();
                    if (ArrayUtils.isNotEmpty(interfaces)) {
                        for (Class<?> interfaceClazz : interfaces) {
                            Tag anno = interfaceClazz.getAnnotation(Tag.class);
                            if (Objects.nonNull(anno)) {
                                tag = anno;
                                break;
                            }
                        }
                    }
                }
                return tag;
            }

            private Set<Class<?>> scanPackageByAnnotation(
                    String packageName, final Class<? extends Annotation> annotationClass) {
                ClassPathScanningCandidateComponentProvider scanner =
                        new ClassPathScanningCandidateComponentProvider(false);
                scanner.addIncludeFilter(new AnnotationTypeFilter(annotationClass));
                Set<Class<?>> classes = new HashSet<>();
                for (BeanDefinition beanDefinition : scanner.findCandidateComponents(packageName)) {
                    try {
                        Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                        classes.add(clazz);
                    } catch (ClassNotFoundException ignore) {

                    }
                }
                return classes;
            }
        };
    }

}
