package com.nb6868.onex.common.util;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpContextUtils
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class HttpContextUtils {

    /**
     * 获得http请求
     * @return request
     */
    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }

        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获得完整的url地址
     *
     * @param request 请求
     * @return 完整的url地址
     */
    public static String getFullUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (StrUtil.isBlank(queryString)) {
            return request.getRequestURL().toString();
        } else {
            return request.getRequestURL().append("?").append(queryString).toString();
        }
    }

    /**
     * 获得参数列表
     *
     * @param request       请求
     * @param excludeParams 排除的参数
     * @return 参数列表
     */
    public static Map<String, String> getParameterMap(HttpServletRequest request, String... excludeParams) {
        Enumeration<String> parameters = request.getParameterNames();

        Map<String, String> params = new HashMap<>();
        while (parameters.hasMoreElements()) {
            String parameter = parameters.nextElement();
            boolean excluded = false;
            for (String excludeParam : excludeParams) {
                if (excludeParam.equalsIgnoreCase(parameter)) {
                    excluded = true;
                    break;
                }
            }
            if (!excluded) {
                String value = request.getParameter(parameter);
                if (StrUtil.isNotBlank(value)) {
                    params.put(parameter, value);
                }
            }
        }

        return params;
    }

    /**
     * 获得请求头
     * @param request 请求
     * @return ua
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.USER_AGENT);
    }

    /**
     * 请求头是否包含特定内容
     * @param request 请求
     * @return ua
     */
    public static boolean isUserAgentMatch(HttpServletRequest request, String key) {
        return getUserAgent(request).toLowerCase().contains(key.toLowerCase());
    }

    /**
     * 判断是否微信浏览器
     * @param request 请求
     * @return 是否微信浏览器
     */
    public static boolean isUserAgentMatchWx(HttpServletRequest request) {
        return isUserAgentMatch(request, "micromessenger");
    }

    public static String getLanguage() {
        // 默认语言
        String defaultLanguage = "zh-CN";
        // request
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return defaultLanguage;
        }

        //请求语言
        defaultLanguage = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);

        return defaultLanguage;
    }

    /**
     * 获取IP地址
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        String ip = null;
        String unknown = "unknown";
        try {
            ip = request.getHeader("x-forwarded-for");
            if (StrUtil.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StrUtil.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StrUtil.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StrUtil.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StrUtil.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if ("127.0.0.1".equalsIgnoreCase(ip)) {
                    // 根据网卡取本机配置的IP
                    try {
                        ip = InetAddress.getLocalHost().getHostAddress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            // 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ip != null && ip.length() > 15) {
                if (ip.indexOf(",") > 0) {
                    ip = ip.substring(0, ip.indexOf(","));
                }
            }
        } catch (Exception e) {
            // logger.error("IPUtils ERROR ", e);
        }

        return ip;
    }

    public static String getName(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取请求中的参数
     */
    public static String getRequestParameter(String name) {
        HttpServletRequest request = getHttpServletRequest();
        return getRequestParameter(request, name);
    }

    /**
     * 获取请求中的参数
     */
    public static String getRequestParameter(HttpServletRequest httpRequest, String name) {
        if (httpRequest == null || StrUtil.isEmpty(name)) {
            return null;
        }

        // 先从header中获取
        String param = httpRequest.getHeader(name);

        // 如果header中不存在，则从参数中获取
        if (StrUtil.isBlank(param)) {
            param = httpRequest.getParameter(name);
        }

        return param;
    }

    /**
     * 获取请求中的参数
     */
    public static String getRequestParameter(NativeWebRequest httpRequest, String name) {
        if (httpRequest == null || StrUtil.isEmpty(name)) {
            return null;
        }

        // 先从header中获取
        String param = httpRequest.getHeader(name);

        // 如果header中不存在，则从参数中获取
        if (StrUtil.isBlank(param)) {
            param = httpRequest.getParameter(name);
        }

        return param;
    }

}
