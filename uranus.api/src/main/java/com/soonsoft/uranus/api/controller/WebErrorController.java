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

import com.soonsoft.uranus.web.error.WebErrorCommonHandler;
import com.soonsoft.uranus.web.error.vo.JsonErrorModel;
import com.soonsoft.uranus.web.error.vo.WebErrorModel;
import com.soonsoft.uranus.api.config.properties.ErrorPageProperties;

@ControllerAdvice
@Controller
public class WebErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error/{status_code}";

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
        Throwable exception = this.getException(status.value(), request);
        WebErrorModel model = WebErrorCommonHandler.buildWebErrorModel(status, exception, errorPageProperties);

        if(status == HttpStatus.INTERNAL_SERVER_ERROR) {
            LOGGER.error("发生错误", model.getException());
        }

        response.setStatus(status.value());
        return new ResponseEntity<>(new JsonErrorModel(model), status);
    }

    protected HttpStatus getStatus(Integer statusCode, HttpServletRequest request) {
        if(statusCode == null) {
            return WebErrorCommonHandler.findHttpStatus(request);
        }
        try {
            return HttpStatus.valueOf(statusCode.intValue());
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    protected Throwable getException(Integer statusCode, HttpServletRequest request) {
        if(statusCode == null || !statusCode.equals(500)) {
            return null;
        }

        WebRequest webRequest = new ServletWebRequest(request);
        Throwable exception = this.errorAttributes.getError(webRequest);
        if(exception == null) {
            exception = WebErrorCommonHandler.findException(request);
        }
        return exception;
    }
}
