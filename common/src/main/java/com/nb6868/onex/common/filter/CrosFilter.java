package com.nb6868.onex.common.filter;

import com.nb6868.onex.common.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * cros filter
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class CrosFilter implements Filter {

    CrosProps crosProps;

    @Override
    public void init(FilterConfig config) {
        crosProps = (CrosProps) SpringContextUtils.getBean("crosProps");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        response.setHeader("Access-Control-Allow-Credentials", crosProps.getAllowCredentials());
        response.setHeader("Access-Control-Allow-Headers", crosProps.getAllowHeaders());
        response.setHeader("Access-Control-Allow-Methods", crosProps.getAllowMethods());
        // Access-Control-Allow-Origin和Access-Control-Allow-Credentials有约束;Credentials true,Origin必须指定具体来源,不能用*通配;
        response.setHeader("Access-Control-Allow-Origin", request.getHeader(HttpHeaders.ORIGIN));
        response.setHeader("Access-Control-Max-Age", crosProps.getMaxAge());
        //  放行所有Options请求，提高接口访问速度
        if (RequestMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() {
    }

}
