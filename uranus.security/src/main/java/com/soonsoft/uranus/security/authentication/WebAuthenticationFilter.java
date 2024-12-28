package com.soonsoft.uranus.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.func.Func3;
import com.soonsoft.uranus.core.functional.func.Func4;
import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.security.authentication.jwt.JWTAuthenticationToken;
import com.soonsoft.uranus.security.authentication.jwt.JWTTokenProvider;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.security.entity.security.SecurityUser;

public class WebAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AntPathRequestMatcher loginPasswordMatcher;
    private AntPathRequestMatcher loginVerifyCodeCellPhoneMatcher;
    private AntPathRequestMatcher loginVerifyCodeEmailMatcher;
    private AntPathRequestMatcher loginTokenRefreshMatcher;

    private final static String SECURITY_PROCESSING_TYPE = "X-URANUS-API-LOGIN-TYPE";
    private final static String LOGIN_PASSWORD_TYPE = "login-password";
    private final static String LOGIN_VERIFY_CODE_PHONE_TYPE = "login-verify-code-by-phone";
    private final static String LOGIN_VERIFY_CODE_EMAIL_TYPE = "login-verify-code-email";
    private final static String REFRESH_TYPE = "login-token-refresh";

    private final static String DEFAULT_LOGIN_PASSWORD_URL = "/auth/login";

    private final UserLoginFunction userLoginFunction = new UserLoginFunction();
    private ILoginParameterGetter loginParameterGetter;
    private IRefreshTokenGetter refreshTokenGetter;

    private ITokenProvider<?> tokenProvider;

    public WebAuthenticationFilter() {
        this(null);
    }

    public WebAuthenticationFilter(ITokenProvider<?> tokenProvider) {
        //super(DEFAULT_LOGIN_PASSWORD_URL, null);
        this.tokenProvider = tokenProvider;
        setLoginPasswordUrl(DEFAULT_LOGIN_PASSWORD_URL);
    }

    public void setLoginPasswordUrl(String loginPasswordUrl) {
        Guard.notEmpty(loginPasswordUrl, "the arguments[loginPasswordUrl] is required.");
        loginPasswordMatcher = new AntPathRequestMatcher(loginPasswordUrl);
    }

    public void setLoginVerifyCodeUrl(String loginVerifyCodeUrl) {
        Guard.notEmpty(loginVerifyCodeUrl, "the arguments[loginVerifyCodeUrl] is required.");
        loginVerifyCodeCellPhoneMatcher = new AntPathRequestMatcher(loginVerifyCodeUrl);
    }

    public void setLoginVerifyCodeEmailUrl(String url) {
        Guard.notEmpty(url, "the arguments[url] is required.");
        loginVerifyCodeEmailMatcher = new AntPathRequestMatcher(url);
    }

    public void setLoginTokenRefreshUrl(String loginTokenRefreshUrl) {
        Guard.notEmpty(loginTokenRefreshUrl, "the arguments[loginTokenRefreshUrl] is required.");
        loginTokenRefreshMatcher = new AntPathRequestMatcher(loginTokenRefreshUrl);
    }

    public void setLoginPasswordFn(Func3<String, String, IUserManager, UserInfo> fn) {
        userLoginFunction.setLoginPasswordFn(fn);
    }

    public void setLoginCellPhoneVerifyCodeFn(Func4<String, String, String, IUserManager, UserInfo> fn) {
        userLoginFunction.setLoginCellPhoneVerifyCodeFn(fn);
    }

    public void setLoginEmailVerifyCodeFn(Func3<String, String, IUserManager, UserInfo> fn) {
        userLoginFunction.setLoginEmailVerifyCodeFn(fn);
    }

    public void setLoginParameterGetter(ILoginParameterGetter loginParameterGetter) {
        this.loginParameterGetter = loginParameterGetter;
    }

    public void setRefreshTokenGetter(IRefreshTokenGetter refreshTokenGetter) {
        this.refreshTokenGetter = refreshTokenGetter;
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
        UserInfo userInfo = null;
        IUserManager userManager = SecurityManager.current().getUserManager();
        
        String verifyCode;

        switch (processingType) {
            case LOGIN_PASSWORD_TYPE:
                String userName = loginParameterGetter.getUserName(request);
                String password = loginParameterGetter.getPassword(request);
                userInfo = userLoginFunction.loginPassword(userName, password, userManager);
                break;
            case LOGIN_VERIFY_CODE_PHONE_TYPE:
                String areaCode = loginParameterGetter.getAreaCode(request);
                String cellPhone = loginParameterGetter.getPhoneNumber(request);
                verifyCode = loginParameterGetter.getVerifyCode(request);
                userInfo = userLoginFunction.loginVerifyCodeWithCellPhone(areaCode, cellPhone, verifyCode, userManager);
                break;
            case LOGIN_VERIFY_CODE_EMAIL_TYPE:
                String email = loginParameterGetter.getEmail(request);
                verifyCode = loginParameterGetter.getVerifyCode(request);
                userInfo = userLoginFunction.loginVerifyCodeWithEmail(email, verifyCode, userManager);
                break;
            default:
                throw new BadCredentialsException("login failure, no support function.");
        }

        if(userInfo == null) {
            throw new BadCredentialsException("login failure.");
        }
        SecurityUser user = new SecurityUser(userInfo);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        if(ITokenProvider.JWT_TYPE.equals(tokenProvider.getTokenType())) {
            JWTAuthenticationToken jwtAuthenticationToken = 
                new JWTAuthenticationToken(authentication.getPrincipal(), authentication.getAuthorities());
            ((JWTTokenProvider) tokenProvider).getTokenStrategy().updateRefreshToken(jwtAuthenticationToken);
            authentication = jwtAuthenticationToken;
        }
        return authentication;
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if(loginPasswordMatcher != null && loginPasswordMatcher.matches(request)) {
            request.setAttribute(SECURITY_PROCESSING_TYPE, LOGIN_PASSWORD_TYPE);
        } else if(loginVerifyCodeCellPhoneMatcher != null && loginVerifyCodeCellPhoneMatcher.matches(request)) {
            request.setAttribute(SECURITY_PROCESSING_TYPE, LOGIN_VERIFY_CODE_PHONE_TYPE);
        } else if(loginVerifyCodeEmailMatcher != null && loginVerifyCodeEmailMatcher.matches(request)) {
            request.setAttribute(SECURITY_PROCESSING_TYPE, LOGIN_VERIFY_CODE_EMAIL_TYPE);
        } else if(loginTokenRefreshMatcher != null && loginTokenRefreshMatcher.matches(request)) {
            request.setAttribute(SECURITY_PROCESSING_TYPE, REFRESH_TYPE);
        } else {
            return false;
        }
        return true;
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
    
}
