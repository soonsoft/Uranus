package com.soonsoft.uranus.web.error;

import javax.servlet.http.HttpServletRequest;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.error.BusinessException;
import com.soonsoft.uranus.core.functional.predicate.Predicate1;
import com.soonsoft.uranus.web.error.properties.ErrorMessageProperties;
import com.soonsoft.uranus.web.error.vo.WebErrorModel;

import org.springframework.http.HttpStatus;

public abstract class WebErrorCommonHandler {

    //从Request获取异常信息
    private final static String EXCEPTION = "javax.servlet.error.exception";
    // 从Request获取错误代码
    private final static String HTTP_STATUS_CODE = "javax.servlet.error.status_code";

    private final static Predicate1<Throwable> messageExceptionPredicate = e -> {
        return e instanceof BusinessException || e instanceof IllegalArgumentException || e instanceof WebActionException;
    };

    public static WebErrorModel buildWebErrorModel(HttpStatus status, Throwable exception, ErrorMessageProperties errorPageProperties) {
        return buildWebErrorModel(status, exception, errorPageProperties, messageExceptionPredicate);
    }

    public static WebErrorModel buildWebErrorModel(
        HttpStatus status, 
        Throwable exception, 
        ErrorMessageProperties errorPageProperties, 
        Predicate1<Throwable> messageExceptionPredicate) {
        
        
        WebErrorModel model = new WebErrorModel();
        model.setStatusCode(status.value());
        model.setStatusName(status.getReasonPhrase());
        model.setException(exception);

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
                message = errorPageProperties.getUnfoundMessage();
                break;
            case 500:
                message = errorPageProperties.getServerErrorMessage();
                break;
            default:
                break;
        }

        // 如果有异常信息，则用异常信息覆盖
        if(model.getException() != null && (messageExceptionPredicate != null && messageExceptionPredicate.test(model.getException()))) {
            message = model.getException().getMessage();
        }

        if(StringUtils.isEmpty(message)) {
            message = "Unknown Internal Server Error";
        }
        model.setMessage(message);

        return model;
    }

    public static Exception findException(HttpServletRequest request) {
        return (Exception) request.getAttribute(EXCEPTION);
    }

    public static HttpStatus findHttpStatus(HttpServletRequest request) {
        Integer statusCode = (Integer)request.getAttribute(HTTP_STATUS_CODE);
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


    
}
