package com.soonsoft.uranus.security.config.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.security.jwt.ITokenStrategy;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// see #org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
public class WebApiUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String SECURITY_FORM_USERNAME_NAME = "username";
    public static final String SECURITY_FORM_PASSWORD_NAME = "password";

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login", "POST");

    private ITokenStrategy tokenStrategy;
    private IUsernamePasswordGetter usernamePasswordGetterHandler;

    public WebApiUsernamePasswordAuthenticationFilter(ITokenStrategy tokenStrategy) {
        this(tokenStrategy, new FormUsernamePasswordGetter());
    }

    public WebApiUsernamePasswordAuthenticationFilter(ITokenStrategy tokenStrategy, IUsernamePasswordGetter getter) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.tokenStrategy = tokenStrategy;
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

        //TODO UsernamePasswordAuthenticationToken authRequest = tokenStrategy.getToken(request, response, authentication)
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                username, password);
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(authRequest);
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
