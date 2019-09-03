package com.qiaofengfangxh.springmvc.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QFAutowired {

    String value() default "";
}
