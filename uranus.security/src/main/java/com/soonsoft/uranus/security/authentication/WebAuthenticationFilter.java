package com.soonsoft.uranus.security.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.functional.func.Func3;
import com.soonsoft.uranus.core.functional.func.Func4;
import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.security.authentication.jwt.JWTAuthenticationToken;
import com.soonsoft.uranus.security.authentication.jwt.JWTTokenProvider;
import com.soonsoft.uranus.security.config.constant.SecurityConfigUrlConstant;
import com.soonsoft.uranus.security.entity.security.SecurityUser;

public class WebAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private AntPathRequestMatcher loginPasswordMatcher;
    private AntPathRequestMatcher loginVerifyCodeMatcher;
    private AntPathRequestMatcher loginTokenRefreshMatcher;

    private final static String SECURITY_PROCESSING_TYPE = "X-URANUS-API-LOGIN-TYPE";
    private final static String LOGIN_PASSWORD_TYPE = "login-password";
    private final static String LOGIN_VERIFY_CODE_TYPE = "login-verify-code";
    private final static String REFRESH_TYPE = "login-token-refresh";

    private final UserLoginFunction userLoginFunction = new UserLoginFunction();
    private ILoginParameterGetter loginParameterGetter;

    private ITokenProvider<?> tokenProvider;

    public WebAuthenticationFilter() {
        this(null);
    }

    public WebAuthenticationFilter(ITokenProvider<?> tokenProvider) {
        super(SecurityConfigUrlConstant.LoginPasswordUrl);
        this.tokenProvider = tokenProvider;
    }

    public void setLoginPasswordUrl(String loginPasswordUrl) {
        Guard.notEmpty(loginPasswordUrl, "the arguments[loginPasswordUrl] is required.");
        loginPasswordMatcher = new AntPathRequestMatcher(loginPasswordUrl);
    }

    public void setLoginVerifyCodeUrl(String loginVerifyCodeUrl) {
        Guard.notEmpty(loginVerifyCodeUrl, "the arguments[loginVerifyCodeUrl] is required.");
        loginVerifyCodeMatcher = new AntPathRequestMatcher(loginVerifyCodeUrl);

    }

    public void setLoginTokenRefreshUrl(String loginTokenRefreshUrl) {
        Guard.notEmpty(loginTokenRefreshUrl, "the arguments[loginTokenRefreshUrl] is required.");
        loginTokenRefreshMatcher = new AntPathRequestMatcher(loginTokenRefreshUrl);
    }

    public void setLoginPasswordFn(Func3<String, String, IUserManager, SecurityUser> fn) {
        userLoginFunction.setLoginPasswordFn(fn);
    }

    public void setLoginCellPhoneVerifyCodeFn(Func4<String, String, String, IUserManager, SecurityUser> fn) {
        userLoginFunction.setLoginCellPhoneVerifyCodeFn(fn);
    }

    public void setLoginEmailVerifyCodeFn(Func3<String, String, IUserManager, SecurityUser> fn) {
        userLoginFunction.setLoginEmailVerifyCodeFn(fn);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        SecurityUser user;

        String processingType = (String) request.getAttribute(SECURITY_PROCESSING_TYPE);
        request.removeAttribute(SECURITY_PROCESSING_TYPE);

        IUserManager userManager = SecurityManager.current().getUserManager();

        switch (processingType) {
            case LOGIN_PASSWORD_TYPE:
                String userName = loginParameterGetter.getUserName(request);
                String password = loginParameterGetter.getPassword(request);
                user = userLoginFunction.loginPassword(userName, password, userManager);
                break;
            case LOGIN_VERIFY_CODE_TYPE:
                String areaCode = loginParameterGetter.getAreaCode(request);
                String cellPhone = loginParameterGetter.getPhoneNumber(request);
                String verifyCode = loginParameterGetter.getVerifyCode(request);
                user = userLoginFunction.loginVerifyCodeWithCellPhone(areaCode, cellPhone, verifyCode, userManager);
                break;
            default:
                throw new BadCredentialsException("login failure, no support function.");
        }

        if(user == null) {
            throw new BadCredentialsException("login failure.");
        }
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
        } else if(loginVerifyCodeMatcher != null && loginVerifyCodeMatcher.matches(request)) {
            request.setAttribute(SECURITY_PROCESSING_TYPE, LOGIN_VERIFY_CODE_TYPE);
        } else if(loginTokenRefreshMatcher != null && loginTokenRefreshMatcher.matches(request)) {
            request.setAttribute(SECURITY_PROCESSING_TYPE, REFRESH_TYPE);
        } else {
            return false;
        }
        return true;
    }
    
}
