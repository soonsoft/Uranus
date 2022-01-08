package com.soonsoft.uranus.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Http上下文，可以直接获取Request、Response、Session对象
 */
public class HttpContext {
    
    private static final ThreadLocal<HttpContext> contextHolder = new ThreadLocal<>();

    private HttpServletRequest request;

    private HttpServletResponse response;

    private Map<String, Object> attributes;

    public HttpContext(HttpServletRequest req, HttpServletResponse resp) {
        this.request = req;
        this.response = resp;
        this.attributes = new HashMap<>();
    }

    /**
     * @return the request
     */
    public HttpServletRequest getRequest() {
        if(request == null) {
            // 用ServletRequestAttributes降级
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(attributes != null) {
                return attributes.getRequest();
            }
        }
        return request;
    }

    /**
     * @return the response
     */
    public HttpServletResponse getResponse() {
        if(response == null) {
            // 用ServletRequestAttributes降级
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(attributes != null) {
                return attributes.getResponse();
            }
        }
        return response;
    }

    /**
     * @return the session
     */
    public HttpSession getSession() {
        HttpServletRequest httpRequest = getRequest();
        return httpRequest != null ? httpRequest.getSession() : null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String name) {
        return (T) attributes.get(name);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public static HttpContext current() {
        HttpContext context = contextHolder.get();
        return context;
    }

    public static void setContext(HttpContext context) {
        contextHolder.set(context);
    }

    public static void clearContext() {
        contextHolder.remove();
    }
}