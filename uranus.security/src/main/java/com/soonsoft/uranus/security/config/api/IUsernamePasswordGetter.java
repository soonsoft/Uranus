package com.soonsoft.uranus.security.config.api;

import javax.servlet.http.HttpServletRequest;

public interface IUsernamePasswordGetter {

    UsernamePassword get(HttpServletRequest request);
    
}
