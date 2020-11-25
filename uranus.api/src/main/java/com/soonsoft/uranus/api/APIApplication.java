package com.soonsoft.uranus.api;

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
        "com.soonsoft.uranus.api.config",
        "com.soonsoft.uranus.services",
        "com.soonsoft.uranus.api.controller"
})
public class ApiApplication {

    public static void main(String[] args) {
        onStop();
        SpringApplication.run(ApiApplication.class, args);
    }

    private static void onStop() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
               System.out.println("APIApplication shutdown.");
            }
        });
    }
    
}