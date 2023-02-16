package com.soonsoft.uranus.site.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.site.config.WebErrorConfiguration;
import com.soonsoft.uranus.site.config.properties.ErrorPageProperties;
import com.soonsoft.uranus.web.error.WebErrorCommonHandler;
import com.soonsoft.uranus.web.error.vo.WebErrorModel;
import com.soonsoft.uranus.web.mvc.model.JsonResult;

import java.util.Iterator;
import java.util.Map;

@ControllerAdvice
@Controller
public class WebErrorController extends AbstractErrorController {

    private static final String ERROR_PATH = "/error/{status_code}";

    private static final String DEFAULT_ERROR_PAGE = "error/default";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebErrorController.class);

    private final WebErrorConfiguration errorConfiguration;
    private final ErrorAttributes errorAttributes;
    private final ErrorPageProperties errorPageProperties;

    public WebErrorController(
        WebErrorConfiguration errorConfiguration,
        ErrorPageProperties errorPageProperties,
        ErrorAttributes errorAttributes) {

        super(errorAttributes);
        this.errorConfiguration = errorConfiguration;
        this.errorPageProperties = errorPageProperties;
        this.errorAttributes = errorAttributes;
    }


    @RequestMapping(produces = {"text/html"}, value = "${server.error.path:${error.path:/error}}")
    public ModelAndView errorHtml(
            HttpServletRequest request,
            HttpServletResponse response) {

        return errorHtml(null, request, response);
    }

    /**
     * Error Page
     * @param statusCode
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(produces = {"text/html"}, value = ERROR_PATH)
    public ModelAndView errorHtml(
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
        ModelAndView modelAndView = this.resolveErrorView(request, response, status, model);
        return modelAndView == null
                ? new ModelAndView(DEFAULT_ERROR_PAGE, model)
                : modelAndView;
    }

    @RequestMapping(value = "${server.error.path:${error.path:/error}}")
    public ResponseEntity<JsonResult> error(
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
    public ResponseEntity<JsonResult> error(
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

        JsonResult jsonResult = (JsonResult) JsonResult.create();
        jsonResult.setMessage(model.getMessage());
        jsonResult.put("statusCode", model.getStatusCode());
        jsonResult.setSuccess(false);
        return new ResponseEntity<>(jsonResult, status);
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

    protected ModelAndView resolveErrorView(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpStatus status,
            Map<String, Object> model) {
        Iterator<ErrorViewResolver> iterator = this.errorConfiguration.getErrorViewResolvers().iterator();

        ModelAndView modelAndView;
        do {
            if (!iterator.hasNext()) {
                return null;
            }

            ErrorViewResolver resolver = (ErrorViewResolver)iterator.next();
            modelAndView = resolver.resolveErrorView(request, status, model);
        } while(modelAndView == null);

        return modelAndView;
    }

}
