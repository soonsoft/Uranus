package com.soonsoft.uranus.security.config;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.functional.action.Action4;
import com.soonsoft.uranus.core.functional.func.Func2;
import com.soonsoft.uranus.core.functional.func.Func3;
import com.soonsoft.uranus.core.functional.func.Func4;
import com.soonsoft.uranus.security.config.site.WebSiteApplicationSecurityConfig;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.security.profile.IUserProfile;
import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.authentication.UserLoginFunction;
import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.authorization.IRoleManager;
import com.soonsoft.uranus.security.config.api.WebApiApplicationSecurityConfig;
import com.soonsoft.uranus.security.config.properties.SecurityProperties;

public class WebApplicationSecurityConfigFactory {

    private WebApplicationSecurityConfigType type;
    private ICustomConfigurer[] configurers;
    private UserLoginFunction userLoginFunction = new UserLoginFunction();
    private Action4<IUserManager, IRoleManager, IFunctionManager, IUserProfile> initModuleAction;

    public WebApplicationSecurityConfigFactory(WebApplicationSecurityConfigType type, ICustomConfigurer... configurers) {
        Guard.notNull(type, "the parameter type is required.");
        this.type = type;
        this.configurers = configurers;
    }

    public WebApplicationSecurityConfig create(SecurityProperties securityProperties) {
        WebApplicationSecurityConfig config = type.createConfig(userLoginFunction, configurers);
        config.setSecurityProperties(securityProperties);
        return config;
    }

    public void setInitModuleAction(Action4<IUserManager, IRoleManager, IFunctionManager, IUserProfile> action) {
        this.initModuleAction = action;
    }

    public void applyInitModuleAction() {
        if(initModuleAction != null) {
            SecurityManager securityManager = SecurityManager.current();
            initModuleAction.apply(
                securityManager.getUserManager(), 
                securityManager.getRoleManager(), 
                securityManager.getFunctionManager(),
                securityManager.getUserProfile());
        }
    }

    public void setLoginPasswordFn(Func3<String, String, IUserManager, UserInfo> fn) {
        userLoginFunction.setLoginPasswordFn(fn);
    }

    public void setLoginCellPhoneVerifyCodeFn(Func4<String, String, String, IUserManager, UserInfo> fn) {
        userLoginFunction.setLoginCellPhoneVerifyCodeFn(fn);
    }

    public void setLoginEmailVerifyCodeFn(Func3<String, String, IUserManager, UserInfo> fn) {
        userLoginFunction.setLoginEmailVerifyCodeFn(fn);
    } 

    public enum WebApplicationSecurityConfigType {

        SITE("WebSite", (userLoginFunction, configurerArray) -> new WebSiteApplicationSecurityConfig(userLoginFunction, configurerArray)),
        API("WebAPI", (userLoginFunction, configurerArray) -> new WebApiApplicationSecurityConfig(userLoginFunction, configurerArray)),
        ;

        String typeName;

        Func2<UserLoginFunction, ICustomConfigurer[], WebApplicationSecurityConfig> createFunc;

        private WebApplicationSecurityConfigType(String typeName, Func2<UserLoginFunction, ICustomConfigurer[], WebApplicationSecurityConfig> fn) {
            this.typeName = typeName;
            this.createFunc = fn;
        }

        public WebApplicationSecurityConfig createConfig(UserLoginFunction userLoginFunction, ICustomConfigurer[] configurerArray) {
            return createFunc.call(userLoginFunction, configurerArray);
        }

    }
    
}
