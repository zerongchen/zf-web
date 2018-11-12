package com.aotain.zongfen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/04/12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogAnnotation {
    int module() default 0;
    int type() default 0;
    String dataJson() default "";
    String inputParam() default "";
}
