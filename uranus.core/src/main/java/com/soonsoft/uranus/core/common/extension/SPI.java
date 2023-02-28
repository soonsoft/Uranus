package com.soonsoft.uranus.core.common.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个接口被用于SPI扩展
 * 建立一个同名的文本文件，放于resources/META-INF目录下 <br />
 * 可以支持 key-value 模式
 * xxxKey=com.soonsoft.uranus.XxxClass
 * yyyKey=com.soonsoft.uranus.YyyClass
 * 可以支持list模式，保持配置顺序
 * - com.soonsoft.uranus.XxxClass
 * - com.soonsoft.uranus.YyyClass
 * 可以支持注释
 * # 扩展信息 
 * xxxKey=com.soonsoft.uranus.XxxClass
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {

    /**
     * 默认的扩展名称
     * @return
     */
    String value() default "";
    
}