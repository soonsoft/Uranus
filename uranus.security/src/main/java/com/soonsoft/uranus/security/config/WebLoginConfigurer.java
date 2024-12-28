package com.soonsoft.uranus.security.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.functional.action.Action2;
import com.soonsoft.uranus.core.functional.func.Func3;
import com.soonsoft.uranus.core.functional.func.Func4;
import com.soonsoft.uranus.security.authentication.ILoginParameterGetter;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.authentication.WebAuthenticationFilter;
import com.soonsoft.uranus.security.entity.UserInfo;

public abstract class WebLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, WebLoginConfigurer<H>, WebAuthenticationFilter> {

    private List<Action2<HttpServletRequest, HttpServletResponse>> successEventFnList;
    private List<Action2<HttpServletRequest, HttpServletResponse>> failureEventFnList;

    public WebLoginConfigurer(AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {
        this(new WebAuthenticationFilter(), successHandler, failureHandler);
    }

    protected WebLoginConfigurer(
        WebAuthenticationFilter authenticationFilter, 
        AuthenticationSuccessHandler successHandler, 
        AuthenticationFailureHandler failureHandler) {

        super(authenticationFilter, null);
        successHandler(successHandler);
        failureHandler(failureHandler);
    }

    protected WebLoginConfigurer() {
        this(new WebAuthenticationFilter(), null, null);
    }

    public void addSuccessEvent(Action2<HttpServletRequest, HttpServletResponse> eventFn) {
        if(eventFn == null) {
            return;
        }

        if(successEventFnList == null) {
            successEventFnList = new ArrayList<>();
        }

        successEventFnList.add(eventFn);
    }

    public void addFailedEvent(Action2<HttpServletRequest, HttpServletResponse> eventFn) {
        if(eventFn == null) {
            return;
        }

        if(failureEventFnList == null) {
            failureEventFnList = new ArrayList<>();
        }

        failureEventFnList.add(eventFn);
    }

    protected void onSuccess(HttpServletRequest request, HttpServletResponse response) {
        if(!CollectionUtils.isEmpty(successEventFnList)) {
            for(Action2<HttpServletRequest, HttpServletResponse> action : successEventFnList) {
                action.apply(request, response);
            }
        }
    }

    protected void onFailed(HttpServletRequest request, HttpServletResponse response) {
        if(!CollectionUtils.isEmpty(failureEventFnList)) {
            for(Action2<HttpServletRequest, HttpServletResponse> action : failureEventFnList) {
                action.apply(request, response);
            }
        }
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl);
    }

    public void setLoginPasswordUrl(String url) {
        getAuthenticationFilter().setLoginPasswordUrl(url);
    }

    public void setLoginVerifyCodeByCellPhoneUrl(String url) {
        getAuthenticationFilter().setLoginVerifyCodeUrl(url);
    }

    public void setLoginVerifyCodeByEmailUrl(String url) {
        getAuthenticationFilter().setLoginVerifyCodeEmailUrl(url);
    }

    public void setLoginParameterGetter(ILoginParameterGetter parameterGetter) {
        getAuthenticationFilter().setLoginParameterGetter(parameterGetter);
    }

    public void setLoginPasswordFn(Func3<String, String, IUserManager, UserInfo> fn) {
        getAuthenticationFilter().setLoginPasswordFn(fn);
    }

    public void setLoginCellPhoneVerifyCodeFn(Func4<String, String, String, IUserManager, UserInfo> fn) {
        getAuthenticationFilter().setLoginCellPhoneVerifyCodeFn(fn);
    }

    public void setLoginEmailVerifyCodeFn(Func3<String, String, IUserManager, UserInfo> fn) {
        getAuthenticationFilter().setLoginEmailVerifyCodeFn(fn);
    }
    
}
