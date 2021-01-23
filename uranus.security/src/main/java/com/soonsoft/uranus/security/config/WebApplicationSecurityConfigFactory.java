package com.soonsoft.uranus.security.config;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.security.config.site.WebSiteApplicationSecurityConfig;
import com.soonsoft.uranus.security.config.api.WebApiApplicationSecurityConfig;

public class WebApplicationSecurityConfigFactory {

    private WebApplicationSecurityConfigType type;
    private ICustomConfigurer[] configurers;

    public WebApplicationSecurityConfigFactory(WebApplicationSecurityConfigType type, ICustomConfigurer... configurers) {
        Guard.notNull(type, "the parameter type is required.");
        this.type = type;
        this.configurers = configurers;
    }

    public WebApplicationSecurityConfig create() {
        WebApplicationSecurityConfig config = type.createConfig(configurers);
        return config;
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
