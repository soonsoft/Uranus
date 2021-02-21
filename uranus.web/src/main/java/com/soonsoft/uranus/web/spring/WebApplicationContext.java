package com.soonsoft.uranus.web.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class WebApplicationContext implements ApplicationContextAware {

    private ApplicationContext context = null;

    private static final WebApplicationContext INSTANCE = new WebApplicationContext();

    private WebApplicationContext() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        INSTANCE.context = applicationContext;
    }

    public ApplicationContext getApplictionContext() {
        return context;
    }

    public static ApplicationContext getContext() {
        return INSTANCE.context;
    }

    public static WebApplicationContext getInstance() {
        return INSTANCE;
    }
    
}
