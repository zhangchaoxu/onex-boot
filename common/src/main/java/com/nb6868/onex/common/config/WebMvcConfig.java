package com.nb6868.onex.common.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nb6868.onex.common.log.TraceLogInterceptor;
import com.nb6868.onex.common.oss.OssLocalUtils;
import com.nb6868.onex.common.util.JacksonUtils;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.Validation;
import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * MVC配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
@ConditionalOnProperty(name = "onex.webmvc.enable", havingValue = "true")
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 链路日志拦截器
     */
    @Bean
    public TraceLogInterceptor traceLogInterceptor() {
        return new TraceLogInterceptor();
    }

    /**
     * 拦截器
     *
     * @param registry 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(traceLogInterceptor()).addPathPatterns("/**");
        // registry.addInterceptor(wxWebAuthInterceptor).addPathPatterns("/**");
    }

    /**
     * 拦截参数
     *
     * @param argumentResolvers 参数解析器
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /**
         * 注意,如果使用nginx,需要配置静态文件代理
         * location ~* \.(php|jsp|cgi|asp|aspx|gif|jpg|jpeg|js|css)$
         * {
         * 	proxy_pass http://127.0.0.1:18181;
         *     proxy_set_header Host $host;
         *     proxy_set_header X-Real-IP $remote_addr;
         *     proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
         *     proxy_set_header REMOTE-HOST $remote_addr;
         * }
         */
        // favicon/webjars/static
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/");
        // 貌似只能访问api所在jar的static
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        // 可以访问依赖jar中的webjars
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        // knife4j(swagger) doc
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        // easy poi wps
        // registry.addResourceHandler("/easypoi-preview.html").addResourceLocations("classpath:/META-INF/resources/");
        // registry.addResourceHandler("/easypoijs/**").addResourceLocations("classpath:/META-INF/resources/easypoijs/");
        // 文件读取映射
        if (StrUtil.isAllNotBlank(OssLocalUtils.getOssFileStoragePath(), OssLocalUtils.getOssFileRequestPath())) {
            // 先创建目录，若直接使用./xxx会在api.jar!/BOOT-INF/classes!/下创建文件夹,而不是在jar包下
            // System.getProperty("user.dir")可以获得jar路径，不带斜杠
            File file = FileUtil.mkdir(OssLocalUtils.getOssFileStorageAbsolutePath());
            // 当ossFileStoragePath为相对路径的时候，需要做获得绝对路径 file:/a/b/c
            registry.addResourceHandler(OssLocalUtils.getOssFileRequestPath()).addResourceLocations(file.toURI().toString());
        }
    }

    /**
     * 文件路径在yml配置文件中定义
     * 自定义路径,默认resources/ValidationMessages.properties
     */
    @Autowired
    private MessageSource messageSource;

    /**
     * 为MethodArgumentNotValidException提供Validator
     */
    @Override
    public Validator getValidator() {
        Locale.setDefault(LocaleContextHolder.getLocale());
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        return validator;
    }

    /**
     * 为ConstraintViolationException提供Validator
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(Validation.byProvider(HibernateValidator.class).configure()
                // 校验失败,立即结束,不再进行后续的校验,Provider需为HibernateValidate
                .failFast(true)
                .messageInterpolator(new ResourceBundleMessageInterpolator(new MessageSourceResourceBundleLocator(messageSource)))
                .buildValidatorFactory()
                .getValidator());
        return methodValidationPostProcessor;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new AllEncompassingFormHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        converters.add(jackson2HttpMessageConverter());
    }

    /**
     * 选用jackson实现json的序列化
     */
    @Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        JsonMapper.builder().enable(MapperFeature.USE_ANNOTATIONS);
        JsonMapper.Builder builder = JacksonUtils.getMapperBuilder();
        // enable USE_ANNOTATIONS,否则swagger ApiModelProperty中的内容会无法解析,导致页面上无法显示
        builder.enable(MapperFeature.USE_ANNOTATIONS);
        converter.setObjectMapper(builder.build());
        return converter;
    }

}
