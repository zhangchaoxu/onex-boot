package com.nb6868.onex.common.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class HttpServletRequestReplaceFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            if (StrUtil.equalsIgnoreCase(request.getContentType(), ContentType.JSON.getValue())) {
                chain.doFilter(new OnexHttpServletRequestWrapper((HttpServletRequest) request), response);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
