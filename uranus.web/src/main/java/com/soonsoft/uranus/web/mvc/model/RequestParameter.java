package com.soonsoft.uranus.web.mvc.model;

import javax.servlet.http.HttpServletRequest;

import com.soonsoft.uranus.util.Guard;

/**
 * RequestParameter
 */
public class RequestParameter implements IRequestData {

    private HttpServletRequest request;

    public RequestParameter(HttpServletRequest request) {
        Guard.notNull(request, "the HttpServletRequest is required.");
        this.request = request;
    }

    @Override
    public String get(String parameterName, String defaultValue) {
        String value = this.request.getParameter(parameterName);
        return value != null ? value : defaultValue;
    }
    
}