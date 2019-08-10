package com.soonsoft.uranus.web.util;

import javax.servlet.http.HttpServletRequest;

import java.util.Enumeration;

import com.soonsoft.uranus.util.Guard;
import com.soonsoft.uranus.util.lang.StringUtils;

/**
 * HttpRequestUtils
 */
public abstract class HttpRequestUtils {

    public static boolean isAjax(HttpServletRequest request) {
        Guard.notNull(request, "the HttpServletRequest is required.");

        String accept = request.getHeader("accept");
        return (!StringUtils.isEmpty(accept) && accept.indexOf("application/json") > -1) 
            || StringUtils.equals(request.getHeader("X-Requested-With"), "XMLHttpRequest");
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