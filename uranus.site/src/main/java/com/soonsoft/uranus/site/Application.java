package com.soonsoft.uranus.site;

import com.soonsoft.uranus.security.simple.EnableSimpleSecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Application
 */
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.soonsoft.uranus.site.config",
        "com.soonsoft.uranus.site.controller",
        "com.soonsoft.uranus.services"
})
@EnableSimpleSecurity
public class Application {

    public static void main(String[] args) {
        onStop();
        SpringApplication.run(Application.class, args);
    }

    private static void onStop() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
               System.out.println("application shutdown.");
            }
        });
    }
    
}