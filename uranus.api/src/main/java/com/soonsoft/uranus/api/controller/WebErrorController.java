package com.soonsoft.uranus.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.error.BusinessException;
import com.soonsoft.uranus.api.config.properties.ErrorPageProperties;
import com.soonsoft.uranus.api.viewmodel.WebErrorModel;

/**
 * Created by Soon on 2017/10/2.
 */
@ControllerAdvice
@Controller
public class WebErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error/{status_code}";
    //从Request获取异常信息
    private final static String EXCEPTION = "javax.servlet.error.exception";
    // 从Request获取错误代码
    private final static String HTTP_STATUS_CODE = "javax.servlet.error.status_code";

    private final Logger LOGGER = LoggerFactory.getLogger(WebErrorController.class);

    private final ErrorPageProperties errorPageProperties;
    private final ErrorAttributes errorAttributes;

    public WebErrorController(
        ErrorPageProperties errorPageProperties,
        ErrorAttributes errorAttributes) {

        this.errorPageProperties = errorPageProperties;
        this.errorAttributes = errorAttributes;
    }


    @RequestMapping(value = "${server.error.path:${error.path:/error}}")
    public ResponseEntity<JsonErrorModel> error(
            HttpServletRequest request,
            HttpServletResponse response) {

        return error(null, request, response);
    }

    /**
     * Error Message
     * @param statusCode
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public ResponseEntity<JsonErrorModel> error(
            @PathVariable("status_code") Integer statusCode,
            HttpServletRequest request,
            HttpServletResponse response) {

        HttpStatus status = this.getStatus(statusCode, request);
        WebErrorModel model = buildWebErrorModel(status, request);

        if(status == HttpStatus.INTERNAL_SERVER_ERROR) {
            LOGGER.error("发生错误", model.getException());
        }

        response.setStatus(status.value());

        JsonErrorModel jsonModel = new JsonErrorModel();
        jsonModel.setStatusCode(model.getStatusCode());
        jsonModel.setMessage(model.getMessage());
        return new ResponseEntity<>(jsonModel, status);
    }

    protected HttpStatus getStatus(Integer statusCode, HttpServletRequest request) {
        if(statusCode == null) {
            statusCode = (Integer)request.getAttribute(HTTP_STATUS_CODE);
        }
        if(statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            try {
                return HttpStatus.valueOf(statusCode.intValue());
            } catch (Exception e) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }

    protected Throwable getException(Integer statusCode, HttpServletRequest request) {
        if(statusCode == null || !statusCode.equals(500)) {
            return null;
        }

        WebRequest webRequest = new ServletWebRequest(request);
        Throwable exception = this.errorAttributes.getError(webRequest);
        if(exception == null) {
            exception = (Exception)request.getAttribute(EXCEPTION);
        }
        return exception;
    }

    private WebErrorModel buildWebErrorModel(HttpStatus status, HttpServletRequest request) {
        WebErrorModel model = new WebErrorModel();
        model.setStatusCode(status.value());
        model.setStatusName(status.getReasonPhrase());
        model.setException(getException(status.value(), request));

        String message = null;
        switch (model.getStatusCode()) {
            case 400:
                message = errorPageProperties.getBadRequestMessage();
                break;
            case 401:
                message = errorPageProperties.getUnauthorizedMessage();
                break;
            case 403:
                message = errorPageProperties.getForbiddenMessage();
                break;
            case 404:
                message = errorPageProperties.getNotFoundMessage();
                break;
            case 500:
                message = errorPageProperties.getServerErrorMessage();
                break;
            default:
                break;
        }

        // 如果有异常信息，则用异常信息覆盖
        if(model.getException() != null && isMessageException(model.getException())) {
            message = model.getException().getMessage();
        }

        if(StringUtils.isEmpty(message)) {
            message = "Unknown Internal Server Error";
        }
        model.setMessage(message);

        return model;
    }

    private boolean isMessageException(Throwable e) {
        return e instanceof BusinessException 
            || e instanceof IllegalArgumentException;
    }

    public static class JsonErrorModel {

        private int statusCode;
        private String message;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
