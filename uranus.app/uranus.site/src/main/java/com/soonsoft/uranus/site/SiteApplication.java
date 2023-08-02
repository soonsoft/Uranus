package com.soonsoft.uranus.site;

import java.util.Properties;

import com.soonsoft.uranus.data.EnableDatabaseAccess;
import com.soonsoft.uranus.security.simple.EnableSimpleSecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.soonsoft.uranus.site.config",
    "com.soonsoft.uranus.site.controller",
    "com.soonsoft.uranus.services"
})
@EnableDatabaseAccess
@EnableSimpleSecurity
//@EnableMembership
public class SiteApplication {

    public static void main(String[] args) {
        onStop();
        run(SiteApplication.class, args);
    }

    private static void run(Class<?> clazz, String[] args) {
        Properties properties = System.getProperties();
        ApplicationHome applicationHome = new ApplicationHome(clazz);
        properties.setProperty("uranus.application-home", applicationHome.getDir().getAbsolutePath());

        SpringApplication.run(clazz, args);
    }

    private static void onStop() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
               System.out.println("SiteApplication Shutdown.");
            }
        });
    }
    
}