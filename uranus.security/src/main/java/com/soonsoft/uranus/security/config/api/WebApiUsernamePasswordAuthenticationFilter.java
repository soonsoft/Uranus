package com.soonsoft.uranus.security.config.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.security.config.api.provider.JWTTokenProvider;
import com.soonsoft.uranus.security.jwt.ITokenProvider;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

// 使用AbstractAuthenticationFilterConfigurer，只能继承UsernamePasswordAuthenticationFilter
// 如果想继承AbstractAuthenticationProcessingFilter则需要在Config中指定
//      http.addFilterAt(new WebApiUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
public class WebApiUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String SECURITY_FORM_USERNAME_NAME = "username";
    public static final String SECURITY_FORM_PASSWORD_NAME = "password";

    private ITokenProvider<?> tokenProvider;
    private IUsernamePasswordGetter usernamePasswordGetterHandler;

    public WebApiUsernamePasswordAuthenticationFilter(ITokenProvider<?> tokenProvider) {
        this(tokenProvider, new FormUsernamePasswordGetter());
    }

    public WebApiUsernamePasswordAuthenticationFilter(ITokenProvider<?> tokenProvider, IUsernamePasswordGetter getter) {
        this.tokenProvider = tokenProvider;
        this.usernamePasswordGetterHandler = getter;
    }
    
    public void setUsernamePasswordGetterHandler(IUsernamePasswordGetter usernamePasswordGetterHandler) {
        this.usernamePasswordGetterHandler = usernamePasswordGetterHandler;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = null;
        String password = null;

        UsernamePassword usernamePassword = usernamePasswordGetterHandler.get(request);
        if(usernamePassword != null) {
            username = usernamePassword.getUsername();
            password = usernamePassword.getPassword();
        }
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                username, password);
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy sessionStrategy) {
        // JWT不放Session里
        if(tokenProvider instanceof JWTTokenProvider) {
            return;
        }
        super.setSessionAuthenticationStrategy(sessionStrategy);
    }
    
    private static class FormUsernamePasswordGetter implements IUsernamePasswordGetter {

        @Override
        public UsernamePassword get(HttpServletRequest request) {
            return new UsernamePassword(
                request.getParameter(SECURITY_FORM_USERNAME_NAME), 
                request.getParameter(SECURITY_FORM_PASSWORD_NAME));
        }
        
    }
    
}
