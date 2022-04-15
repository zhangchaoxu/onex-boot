package com.nb6868.onex.common.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HttpServletRequestReplaceFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            requestWrapper = new OnexHttpServletRequestWrapper((HttpServletRequest) request);
        }
        chain.doFilter(requestWrapper == null ? request : requestWrapper, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
