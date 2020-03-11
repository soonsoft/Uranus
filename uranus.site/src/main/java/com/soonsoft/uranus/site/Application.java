package com.soonsoft.uranus.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Application
 */
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.soonsoft.uranus.site.config",
        "com.soonsoft.uranus.services",
        "com.soonsoft.uranus.site.controller"
})
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