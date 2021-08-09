package com.soonsoft.uranus.api.controller.advice;

import com.soonsoft.uranus.api.model.APIResult;
import com.soonsoft.uranus.web.mvc.model.IResultData;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class APIResultAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if(returnType != null) {
            Class<?> returnClass = returnType.getParameterType();
            return returnClass != ResponseEntity.class && !returnClass.isAssignableFrom(View.class) && !returnClass.isAssignableFrom(APIResult.class);
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(
        Object body, MethodParameter returnType, 
        MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType, 
        ServerHttpRequest request, 
        ServerHttpResponse response) {
        

        if(body instanceof APIResult) {
            return body;
        }

        if(body instanceof IResultData) {
            IResultData jsonResult = (IResultData) body;
            if(!jsonResult.isSuccess()) {
                return APIResult.create(500, jsonResult.getMessage());
            }
            return APIResult.create(jsonResult.getData());
        }

        return APIResult.create(body);

    }
    
}
