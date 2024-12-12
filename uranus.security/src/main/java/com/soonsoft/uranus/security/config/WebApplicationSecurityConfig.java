package com.soonsoft.uranus.security.config;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.security.authentication.UserLoginFunction;
import com.soonsoft.uranus.security.authorization.WebSecurityMetadataSource;
import com.soonsoft.uranus.security.config.properties.SecurityProperties;

import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

public abstract class WebApplicationSecurityConfig implements ISecurityConfig {

    private WebSecurityMetadataSource webSecurityMetadataSource;
    private AuthorizationManager<RequestAuthorizationContext> webAuthorizationManager;
    private SecurityProperties securityProperties;
    private UserLoginFunction userLoginFunction;
    private List<ICustomConfigurer> configurerList;
 
    //#region getter and setter

    public WebSecurityMetadataSource getWebSecurityMetadataSource() {
        return webSecurityMetadataSource;
    }

    public void setWebSecurityMetadataSource(WebSecurityMetadataSource webSecurityMetadataSource) {
        this.webSecurityMetadataSource = webSecurityMetadataSource;
    }

    public AuthorizationManager<RequestAuthorizationContext> getWebAuthorizationManager() {
        return webAuthorizationManager;
    }

    public void setWebAuthorizationManager(AuthorizationManager<RequestAuthorizationContext> authorizationManager) {
        this.webAuthorizationManager = authorizationManager;
    }

    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public UserLoginFunction getUserLoginFunction() {
        return userLoginFunction;
    }

    public void setUserLoginFunction(UserLoginFunction userLoginFunction) {
        this.userLoginFunction = userLoginFunction;
    }

    //#endregion

    protected void setConfigurerList(ICustomConfigurer... configurers) {
        if(configurers != null && configurers.length > 0) {
            ArrayList<ICustomConfigurer> afterConfigurers = new ArrayList<>();
            for(ICustomConfigurer configurer : configurers) {
                afterConfigurers.add(configurer);
            }
            this.configurerList = afterConfigurers;
        }
    }

    protected List<ICustomConfigurer> getConfigurerList() {
        return this.configurerList;
    }

    protected void setConfig(HttpSecurity http) {
        if(configurerList != null) {
            configurerList.forEach(c -> c.config(http, this));
        }
    }

    protected String[] getPermitPatterns() {
        SecurityProperties properties = getSecurityProperties();
        if(properties != null) {
            return properties.getResourcePathArray();
        }
        return null;
    }
}