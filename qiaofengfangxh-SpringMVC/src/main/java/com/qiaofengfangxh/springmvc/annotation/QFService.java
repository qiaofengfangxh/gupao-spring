package com.qiaofengfangxh.springmvc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QFService {

    String value() default "";
}
