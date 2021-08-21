package com.soonsoft.uranus.site.controller.base;

import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.web.mvc.controller.AbstractController;

/**
 * BaseController
 */
public abstract class BaseController extends AbstractController {

    public UserInfo getCurrentUser() {
        return SecurityManager.current().getCurrentUser();
    }
    
}