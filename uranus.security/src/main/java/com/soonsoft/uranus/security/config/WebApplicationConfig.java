package com.soonsoft.uranus.security.config;

import java.util.List;

import com.soonsoft.uranus.security.authorization.WebAccessDecisionManager;
import com.soonsoft.uranus.security.authorization.WebSecurityMetadataSource;

import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * WebApplicationConfig
 */
public abstract class WebApplicationConfig implements ISecurityConfig {

    private WebAccessDecisionManager webAccessDecisionManager;

    private WebSecurityMetadataSource webSecurityMetadataSource;

    private List<ICustomConfigurer> configurerList;

    // 替换FilterSecurityInterceptor中的AccessDecisionManager和SecurityMetadataSource
    private class FilterSecurityInterceptorPostProcessor implements ObjectPostProcessor<FilterSecurityInterceptor> {

        @Override
        public <O extends FilterSecurityInterceptor> O postProcess(O filterSecurityInterceptor) {
            filterSecurityInterceptor.setAccessDecisionManager(webAccessDecisionManager);
            webSecurityMetadataSource.setDefaultSecurityMetadataSource(filterSecurityInterceptor.getSecurityMetadataSource());
            filterSecurityInterceptor.setSecurityMetadataSource(webSecurityMetadataSource);
            return filterSecurityInterceptor;
        }
        
    }

    //#region getter and setter

    public WebAccessDecisionManager getWebAccessDecisionManager() {
        return webAccessDecisionManager;
    }

    public void setWebAccessDecisionManager(WebAccessDecisionManager webAccessDecisionManager) {
        this.webAccessDecisionManager = webAccessDecisionManager;
    }

    public WebSecurityMetadataSource getWebSecurityMetadataSource() {
        return webSecurityMetadataSource;
    }

    public void setWebSecurityMetadataSource(WebSecurityMetadataSource webSecurityMetadataSource) {
        this.webSecurityMetadataSource = webSecurityMetadataSource;
    }

    //#endregion

    protected void setConfigurerList(ICustomConfigurer... configurers) {
        if(configurers != null && configurers.length > 0) {
            this.configurerList = List.of(configurers);
        }
    }

    protected void setConfig(HttpSecurity http) {
        if(configurerList != null) {
            configurerList.forEach(c -> c.config(http));
        }
    }

    protected ObjectPostProcessor<FilterSecurityInterceptor> getPostProcessor() {
        return new FilterSecurityInterceptorPostProcessor();
    }
}