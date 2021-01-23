package com.soonsoft.uranus.security.simple;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.soonsoft.uranus.security.config.properties.SecurityProperties;
import com.soonsoft.uranus.security.simple.config.SimpleSecutityConfiguration;

import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(value = {SimpleSecutityConfiguration.class, SecurityProperties.class})
public @interface EnableSimpleSecurity {
    
}
