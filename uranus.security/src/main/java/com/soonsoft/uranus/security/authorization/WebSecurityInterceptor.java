package com.soonsoft.uranus.security.authorization;

import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;

/**
 * WebSecurityInterceptor
 */
@Deprecated
public class WebSecurityInterceptor extends AbstractSecurityInterceptor {

    private SecurityMetadataSource securityMetadataSource;

    /**
     * @return the securityMetadataSource
     */
    public SecurityMetadataSource getSecurityMetadataSource() {
        return securityMetadataSource;
    }

    /**
     * @param securityMetadataSource the securityMetadataSource to set
     */
    public void setSecurityMetadataSource(SecurityMetadataSource securityMetadataSource) {
        this.securityMetadataSource = securityMetadataSource;
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return null;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return null;
    }

    
}