package com.soonsoft.uranus.security.config.api;

import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

public class WebApiSecurityContextPersistenceFilter extends SecurityContextPersistenceFilter {

    public WebApiSecurityContextPersistenceFilter(SecurityContextRepository repo) {
		super(repo);
	}
    
}
