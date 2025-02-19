package com.nb6868.onex.common.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.nio.file.Path;

/**
 * 非静态资源处理配置
 * see https://blog.csdn.net/m0_46803792/article/details/128038477/
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
@ConditionalOnProperty(name = "onex.oss.non-static", havingValue = "true")
public class NonStaticResourceHttpRequestConfig extends ResourceHttpRequestHandler {

    public final static String ATTR_FILE = "NON-STATIC-FILE";

    @Override
    protected Resource getResource(HttpServletRequest request) {
        final String filePath = (String) request.getAttribute(ATTR_FILE);
        return new FileSystemResource(filePath);
    }

}
