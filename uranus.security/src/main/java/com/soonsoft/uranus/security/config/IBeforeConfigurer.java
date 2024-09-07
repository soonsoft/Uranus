package com.soonsoft.uranus.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

// TODO 未完成
public interface IBeforeConfigurer extends ICustomConfigurer {
    
    @Override
    default public void config(HttpSecurity http) {
        // 前置处理器只携带配置信息，不进行配置
    }

}
