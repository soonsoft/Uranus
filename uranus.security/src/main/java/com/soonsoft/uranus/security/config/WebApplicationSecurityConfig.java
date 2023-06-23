package com.soonsoft.uranus.security.config;

import java.util.List;

import com.soonsoft.uranus.security.authorization.WebSecurityMetadataSource;

import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

public abstract class WebApplicationSecurityConfig implements ISecurityConfig {

    private WebSecurityMetadataSource webSecurityMetadataSource;
    private AuthorizationManager<RequestAuthorizationContext> webAuthorizationManager;

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

    //#endregion

    protected void setConfigurerList(ICustomConfigurer... configurers) {
        if(configurers != null && configurers.length > 0) {
            this.configurerList = List.of(configurers);
        }
    }

    protected List<ICustomConfigurer> getConfigurerList() {
        return this.configurerList;
    }

    protected void setConfig(HttpSecurity http) {
        if(configurerList != null) {
            configurerList.forEach(c -> c.config(http));
        }
    }
}