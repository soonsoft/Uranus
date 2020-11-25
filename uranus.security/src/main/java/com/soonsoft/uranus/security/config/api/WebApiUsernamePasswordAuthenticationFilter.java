package com.soonsoft.uranus.security.config.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.security.jwt.JWTAuthenticationToken;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class WebApiUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String SECURITY_FORM_USERNAME_NAME = "username";
    public static final String SECURITY_FORM_PASSWORD_NAME = "password";

    private IUsernamePasswordGetter usernamePasswordGetterHandler;

    public WebApiUsernamePasswordAuthenticationFilter() {
        this(new FormUsernamePasswordGetter());
    }

    public WebApiUsernamePasswordAuthenticationFilter(IUsernamePasswordGetter getter) {
        super(new AntPathRequestMatcher("/login", "POST"));
        this.usernamePasswordGetterHandler = getter;
    }
    
    public void setUsernamePasswordGetterHandler(IUsernamePasswordGetter usernamePasswordGetterHandler) {
        this.usernamePasswordGetterHandler = usernamePasswordGetterHandler;
    }

    // @Override
    // public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
    //         throws AuthenticationException {

    //     String username = null;
    //     String password = null;

    //     UsernamePassword usernamePassword = usernamePasswordGetterHandler.get(request);
    //     if(usernamePassword != null) {
    //         username = usernamePassword.getUsername();
    //         password = usernamePassword.getPassword();
    //     }

        UsernamePasswordAuthenticationToken authRequest = new JWTAuthenticationToken(
                username, password);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    //     return super.attemptAuthentication(request, response);
    // }
    
    private static class FormUsernamePasswordGetter implements IUsernamePasswordGetter {

        @Override
        public UsernamePassword get(HttpServletRequest request) {
            return new UsernamePassword(
                request.getParameter(SECURITY_FORM_USERNAME_NAME), 
                request.getParameter(SECURITY_FORM_PASSWORD_NAME));
        }
        
    }
    
}
