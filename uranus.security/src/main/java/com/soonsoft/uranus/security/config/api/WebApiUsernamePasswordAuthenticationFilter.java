package com.soonsoft.uranus.security.config.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class WebApiUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String SECURITY_FORM_USERNAME_NAME = "username";
    public static final String SECURITY_FORM_PASSWORD_NAME = "password";

    private IUsernamePasswordGetter usernamePasswordGetterHandler = new FormUsernamePasswordGetter();

    public WebApiUsernamePasswordAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
        setAuthenticationSuccessHandler(new WebApiAuthenticationSuccessHandler());
        setAuthenticationFailureHandler(new WebApiAuthenticationFailureHandler());
    }
    
    public void setUsernamePasswordGetterHandler(IUsernamePasswordGetter usernamePasswordGetterHandler) {
        this.usernamePasswordGetterHandler = usernamePasswordGetterHandler;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        String username = null;
        String password = null;

        UsernamePassword usernamePassword = usernamePasswordGetterHandler.get(request);
        if(usernamePassword != null) {
            username = usernamePassword.getUsername();
            password = usernamePassword.getPassword();
        }

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        username = username.trim();

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                username, password);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request,
			UsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
    
    private static class FormUsernamePasswordGetter implements IUsernamePasswordGetter {

        @Override
        public UsernamePassword get(HttpServletRequest request) {
            return new UsernamePassword(
                request.getParameter(SECURITY_FORM_USERNAME_NAME), 
                request.getParameter(SECURITY_FORM_PASSWORD_NAME));
        }
        
    }

    // 登录成功处理器
    private static class WebApiAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                Authentication authentication) throws IOException, ServletException {
            final Integer statusCode = HttpStatus.OK.value();
            response.setStatus(statusCode);
            response.getWriter().print(authentication.getDetails());
        }

    }

    // 登录失败处理器
    private static class WebApiAuthenticationFailureHandler implements AuthenticationFailureHandler {

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                AuthenticationException exception) throws IOException, ServletException {
            final Integer statusCode = HttpStatus.UNAUTHORIZED.value();
            response.setStatus(statusCode);
            response.getWriter().print("Unauthorized");
        }

    }
    
}
