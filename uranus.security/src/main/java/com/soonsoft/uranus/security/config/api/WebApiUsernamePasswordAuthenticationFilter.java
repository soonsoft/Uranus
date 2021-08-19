package com.soonsoft.uranus.security.config.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.config.api.jwt.JWTTokenProvider;
import com.soonsoft.uranus.security.config.api.jwt.token.JWTAuthenticationToken;

import org.springframework.security.authentication.BadCredentialsException;
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
            Authentication authentication = refreshAuthenticate(refreshToken);
            if(authentication == null) {
                throw new BadCredentialsException("Refresh Token is invalid.");
            }
            return authentication;
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
        if(ITokenProvider.SESSION_ID_TYPE.equals(tokenProvider.getTokenType())) {
            setDetails(request, authRequest);
        }

        Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);
        if(ITokenProvider.JWT_TYPE.equals(tokenProvider.getTokenType())) {
            JWTAuthenticationToken jwtAuthenticationToken = 
                new JWTAuthenticationToken(authentication.getPrincipal(), authentication.getAuthorities());
            ((JWTTokenProvider) tokenProvider).getTokenStrategy().updateRefreshToken(jwtAuthenticationToken);
            authentication = jwtAuthenticationToken;
        }
        return authentication;
    }

    @Override
    public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy sessionStrategy) {
        // JWT不放Session里
        if(ITokenProvider.JWT_TYPE.equals(tokenProvider.getTokenType())) {
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
        if(StringUtils.isEmpty(refreshToken)) {
            return null;
        }
        String jti = tokenProvider.getTokenStrategy().checkRefreshToken(refreshToken);
        if(jti == null) {
            return null;
        }
        JWTAuthenticationToken authentication = (JWTAuthenticationToken) tokenProvider.getTokenStrategy().refreshToken(jti);
        ((JWTTokenProvider) tokenProvider).getTokenStrategy().updateRefreshToken(authentication);
        return authentication;
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
