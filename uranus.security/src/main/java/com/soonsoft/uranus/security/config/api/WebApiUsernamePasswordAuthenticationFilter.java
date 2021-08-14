package com.soonsoft.uranus.security.config.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.config.api.jwt.JWTTokenProvider;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

// 使用AbstractAuthenticationFilterConfigurer，只能继承UsernamePasswordAuthenticationFilter
// 如果想继承AbstractAuthenticationProcessingFilter则需要在Config中指定
//      http.addFilterAt(new WebApiUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
public class WebApiUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public final static String SECURITY_FORM_USERNAME_NAME = "username";
    public final static String SECURITY_FORM_PASSWORD_NAME = "password";
    public final static String SECURITY_FORM_REFRESH_TOKEN_NAME = "refreshToken";

    private final static String SECURITY_PROCESSING_TYPE = "X-URANUS-API-LOGIN-TYPE";
    private final static String LOGIN_TYPE = "login";
    private final static String REFRESH_TYPE = "refresh";

    private ITokenProvider<?> tokenProvider;
    private IUsernamePasswordGetter usernamePasswordGetterHandler;
    private RequestMatcher refreshTokenRequestMatcher;
    private IRefreshTokenGetter refreshTokenGetter;

    public WebApiUsernamePasswordAuthenticationFilter(ITokenProvider<?> tokenProvider) {
        this(tokenProvider, new FormUsernamePasswordGetter(), null, null);
    }

    public WebApiUsernamePasswordAuthenticationFilter(ITokenProvider<?> tokenProvider, String refreshProcessingUrl) {
        this(tokenProvider, new FormUsernamePasswordGetter(), refreshProcessingUrl, new RefreshTokenGetter());
    }

    public WebApiUsernamePasswordAuthenticationFilter(
        ITokenProvider<?> tokenProvider, IUsernamePasswordGetter usernamePasswordGetter, 
        String refreshProcessingUrl, IRefreshTokenGetter refreshTokenGetter) {

        this.tokenProvider = tokenProvider;
        setUsernamePasswordGetterHandler(usernamePasswordGetter);
        setRefreshProcessingUrl(refreshProcessingUrl);
        this.refreshTokenGetter = refreshTokenGetter;
    }
    
    public void setUsernamePasswordGetterHandler(IUsernamePasswordGetter usernamePasswordGetterHandler) {
        this.usernamePasswordGetterHandler = usernamePasswordGetterHandler;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String processingType = (String) request.getAttribute(SECURITY_PROCESSING_TYPE);
        request.removeAttribute(SECURITY_PROCESSING_TYPE);
        if(REFRESH_TYPE.equals(processingType)) {
            String refreshToken = refreshTokenGetter == null ? null : refreshTokenGetter.getRefreshToken(request);
            return refreshAuthenticate(refreshToken);
        }

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

    public void setRefreshProcessingUrl(String refreshProcessingUrl) {
        if(!StringUtils.isEmpty(refreshProcessingUrl)) {
            refreshTokenRequestMatcher = new AntPathRequestMatcher(refreshProcessingUrl, "POST");
        }
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		boolean result = super.requiresAuthentication(request, response);
        if(result) {
            request.setAttribute(SECURITY_PROCESSING_TYPE, LOGIN_TYPE);
        } else {
            if(refreshTokenRequestMatcher != null) {
                result = refreshTokenRequestMatcher.matches(request);
                if(result) {
                    request.setAttribute(SECURITY_PROCESSING_TYPE, REFRESH_TYPE);
                }
            }
        }
        return result;
	}

    protected Authentication refreshAuthenticate(String refreshToken) {
        // TODO 验证Refresh Token 返回Authentication
        return null;

    }
    
    private static class FormUsernamePasswordGetter implements IUsernamePasswordGetter {

        @Override
        public UsernamePassword get(HttpServletRequest request) {
            return new UsernamePassword(
                request.getParameter(SECURITY_FORM_USERNAME_NAME), 
                request.getParameter(SECURITY_FORM_PASSWORD_NAME));
        }
        
    }

    private static class RefreshTokenGetter implements IRefreshTokenGetter {

        @Override
        public String getRefreshToken(HttpServletRequest request) {
            return request.getParameter(SECURITY_FORM_REFRESH_TOKEN_NAME);
        }
        
    }
    
}
