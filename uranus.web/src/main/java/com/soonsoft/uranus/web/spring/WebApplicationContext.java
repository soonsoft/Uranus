package com.soonsoft.uranus.web.spring;

import java.io.File;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

public class WebApplicationContext implements ApplicationContextAware {

    private ApplicationContext context = null;

    private String applicationRootPath = null;

    private static final WebApplicationContext INSTANCE = new WebApplicationContext();

    private WebApplicationContext() {

    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
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

    public static void initApplicationRootPath(Class<?> applicationClass) {
        File directoryPath = getDirectoryPath(applicationClass);
        if(directoryPath != null) {
            INSTANCE.applicationRootPath = directoryPath.getAbsolutePath();
        }
    }

    public static String getApplicationRootPath() {
        return INSTANCE.applicationRootPath;
    }

    public static File getDirectoryPath(Class<?> sourceClass) {
        WebApplicationHome applicationHome = new WebApplicationHome(sourceClass);
        return applicationHome.getDir();
    }
    
}
