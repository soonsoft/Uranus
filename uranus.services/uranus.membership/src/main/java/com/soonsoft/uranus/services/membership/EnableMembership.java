package com.soonsoft.uranus.services.membership;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.soonsoft.uranus.services.membership.config.MembershipConfiguration;
import com.soonsoft.uranus.services.membership.config.properties.MembershipProperties;

import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(value = {MembershipConfiguration.class, MembershipProperties.class})
public @interface EnableMembership {
    
}
