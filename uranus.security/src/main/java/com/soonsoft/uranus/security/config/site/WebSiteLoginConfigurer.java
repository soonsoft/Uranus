package com.soonsoft.uranus.security.config.site;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.soonsoft.uranus.security.config.WebLoginConfigurer;

public class WebSiteLoginConfigurer extends WebLoginConfigurer<HttpSecurity> {

    public WebSiteLoginConfigurer(String loginSuccessUrl, String loginFailureUrl) {
        successHandler(new WebSiteAuthenticationSuccessHandler(loginSuccessUrl, this));
        failureHandler(new WeSiteAuthenticationFailureHandler(loginFailureUrl, this));
    }

    public void setLoginPage(String loginPage) {
        loginPage(loginPage);
    }

    private static class WebSiteAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

        private final WebSiteLoginConfigurer configurer;

        public WebSiteAuthenticationSuccessHandler(String loginSuccessUrl, WebSiteLoginConfigurer configurer) {
            setDefaultTargetUrl(loginSuccessUrl);
            this.configurer = configurer;
        }

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                Authentication authentication) throws ServletException, IOException {
            super.onAuthenticationSuccess(request, response, authentication);
            configurer.onSuccess(request, response);
        }

    }

    private static class WeSiteAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

        private final WebSiteLoginConfigurer configurer;

        public WeSiteAuthenticationFailureHandler(String loginFailureUrl, WebSiteLoginConfigurer configurer) {
            setDefaultFailureUrl(loginFailureUrl);
            this.configurer = configurer;
        }

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                AuthenticationException exception) throws IOException, ServletException {
            super.onAuthenticationFailure(request, response, exception);
            configurer.onFailed(request, response);
        }

    }
    
}
