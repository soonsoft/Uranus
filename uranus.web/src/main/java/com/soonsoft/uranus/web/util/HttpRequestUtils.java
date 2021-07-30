package com.soonsoft.uranus.web.util;

import javax.servlet.http.HttpServletRequest;

import java.util.Enumeration;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.lang.StringUtils;


public abstract class HttpRequestUtils {

    public static boolean isAjax(HttpServletRequest request) {
        Guard.notNull(request, "the HttpServletRequest is required.");

        String accept = request.getHeader("accept");
        return (!StringUtils.isEmpty(accept) && accept.indexOf("application/json") > -1) 
            || StringUtils.equals(request.getHeader("X-Requested-With"), "XMLHttpRequest");
    }

    public static boolean isMultipart(HttpServletRequest request) {
        Guard.notNull(request, "the HttpServletRequest is required.");
        if(!isPost(request)) {
            return false;
        } else {
            String contentType = request.getContentType();
            return contentType == null ? false : contentType.toLowerCase().startsWith("multipart/");
        }
    }

    public static boolean isGet(HttpServletRequest request) {
        return isMethod(request, "GET");
    }

    public static boolean isPost(HttpServletRequest request) {
        return isMethod(request, "POST");
    }

    public static boolean isPut(HttpServletRequest request) {
        return isMethod(request, "PUT");
    }

    public static boolean isDelete(HttpServletRequest request) {
        return isMethod(request, "DELETE");
    }

    public static boolean isHead(HttpServletRequest request) {
        return isMethod(request, "HEAD");
    }

    public static boolean isMethod(HttpServletRequest request, String method) {
        Guard.notNull(request, "the HttpServletRequest is required.");
        return method == null ? false : method.equalsIgnoreCase(request.getMethod());
    }

    public static String getClientIP(HttpServletRequest request) {
        Guard.notNull(request, "the HttpServletRequest is required.");

        String clientIP = null;
        String unknown = "unknown";
        Enumeration<String> ipList = request.getHeaders("X-Forwarded-For");
        if(ipList != null) {
            while(ipList.hasMoreElements()) {
                clientIP = ipList.nextElement();
                if(!StringUtils.isBlank(clientIP) && !StringUtils.equals(clientIP, unknown)) {
                    return clientIP;
                }
            }
        }

        clientIP = request.getHeader("Proxy-Client-IP");
        if(!StringUtils.isBlank(clientIP) && !StringUtils.equals(clientIP, unknown)) {
            return clientIP;
        }

        clientIP = request.getHeader("WL-Proxy-Client-IP");
        if(!StringUtils.isBlank(clientIP) && !StringUtils.equals(clientIP, unknown)) {
            return clientIP;
        }

        return request.getRemoteAddr();
    }    
}