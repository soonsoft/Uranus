package com.soonsoft.uranus.data.service.meta.anno;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.sql.JDBCType;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface MappingType {

    JDBCType value() default JDBCType.NULL;
    
}
