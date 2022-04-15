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
        if (request instanceof HttpServletRequest) {
            if ("application/json".equalsIgnoreCase(request.getContentType())) {
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
