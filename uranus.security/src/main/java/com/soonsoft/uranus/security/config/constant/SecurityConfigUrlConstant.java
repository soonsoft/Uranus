package com.soonsoft.uranus.security.config.constant;

public interface SecurityConfigUrlConstant {

    // String SiteLoginUrl = "/login";
    // String SiteLogoutUrl = "/logout";

    // String WebApiLoginUrl = "/auth/login";
    // String WebApiRefreshUrl = "/auth/refresh";
    // String WebAplLogoutUrl = "/auth/logout";

    String SiteLoginSuccessUrl = "/";
    String SiteLoginPage = "/auth/login/page";
    String LoginPasswordUrl = "/auth/login/password";
    String LoginVerifyCodeCellPhoneUrl = "/auth/login/verify-code/cellphone";
    String LoginVerifyCodeEmailUrl = "/auth/login/verify-code/email";
    String LogoutUrl = "/auth/logout";
    String LoginTokenRefreshUrl = "/auth/token/refresh";
}
