package com.soonsoft.uranus.security.config;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.functional.action.Action3;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.security.config.site.WebSiteApplicationSecurityConfig;
import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.authorization.IRoleManager;
import com.soonsoft.uranus.security.config.api.WebApiApplicationSecurityConfig;

public class WebApplicationSecurityConfigFactory {

    private WebApplicationSecurityConfigType type;
    private ICustomConfigurer[] configurers;

    private Action3<IUserManager, IRoleManager, IFunctionManager> initModuleAction;

    public WebApplicationSecurityConfigFactory(WebApplicationSecurityConfigType type, ICustomConfigurer... configurers) {
        Guard.notNull(type, "the parameter type is required.");
        this.type = type;
        this.configurers = configurers;
    }

    public WebApplicationSecurityConfig create() {
        WebApplicationSecurityConfig config = type.createConfig(configurers);
        return config;
    }

    public void setInitModuleAction(Action3<IUserManager, IRoleManager, IFunctionManager> action) {
        this.initModuleAction = action;
    }

    public void applyInitModuleAction() {
        if(initModuleAction != null) {
            SecurityManager securityManager = SecurityManager.current();
            initModuleAction.apply(
                securityManager.getUserManager(), 
                securityManager.getRoleManager(), 
                securityManager.getFunctionManager());
        }
    }

    public enum WebApplicationSecurityConfigType {

        SITE("WebSite", configurerArray -> new WebSiteApplicationSecurityConfig(configurerArray)),
        API("WebAPI", configurerArray -> new WebApiApplicationSecurityConfig(configurerArray)),
        ;

        String typeName;

        Func1<ICustomConfigurer[], WebApplicationSecurityConfig> createFunc;

        private WebApplicationSecurityConfigType(String typeName, Func1<ICustomConfigurer[], WebApplicationSecurityConfig> fn) {
            this.typeName = typeName;
            this.createFunc = fn;
        }

        public WebApplicationSecurityConfig createConfig(ICustomConfigurer[] configurerArray) {
            return createFunc.call(configurerArray);
        }

    }
    
}
