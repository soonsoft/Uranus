package com.soonsoft.uranus.api.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.api.config.properties.ErrorPageProperties;
import com.soonsoft.uranus.web.error.WebErrorCommonHandler;
import com.soonsoft.uranus.web.error.vo.WebErrorModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ErrorPageProperties errorPageProperties;

    public GlobalExceptionHandler(ErrorPageProperties errorPageProperties) {
        this.errorPageProperties = errorPageProperties;
    }

    @ResponseBody
    @ExceptionHandler
    public WebErrorModel exceptionHandler(Exception exception, HttpServletRequest request, HttpServletResponse response) {
        LOGGER.error("发生错误", exception);

        HttpStatus httpStatus = exception instanceof HttpStatusCodeException 
            ? ((HttpStatusCodeException) exception).getStatusCode()
            : WebErrorCommonHandler.findHttpStatus(request);
        response.setStatus(httpStatus.value());
        return WebErrorCommonHandler.buildWebErrorModel(httpStatus, exception, errorPageProperties);
    }
    
}
