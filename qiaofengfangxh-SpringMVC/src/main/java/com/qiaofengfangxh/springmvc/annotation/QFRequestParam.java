package com.qiaofengfangxh.springmvc.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QFRequestParam {

    String value() default "";
}
