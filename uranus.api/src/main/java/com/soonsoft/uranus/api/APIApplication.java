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
        /*
            1. 程序正常退出
            2. 使用System.exit()
            3. 终端使用Ctrl+C触发的中断
            4. 系统关闭
            5. OutOfMemory宕机
            6. 使用Kill pid命令干掉进程（注：在使用kill -9 pid时，是不会被调用的）
        */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
               System.out.println("APIApplication shutdown.");
            }
        });
    }
    
}