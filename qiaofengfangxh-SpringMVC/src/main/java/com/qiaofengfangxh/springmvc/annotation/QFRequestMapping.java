package com.qiaofengfangxh.springmvc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QFRequestMapping {

    String value() default "";
}
