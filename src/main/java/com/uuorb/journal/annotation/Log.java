package com.uuorb.journal.annotation;
import java.lang.annotation.*;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    // POST GET PUT DELETE 来判断
    String logType() default "GET";

    // 业务类型，根据Controller的名字来区分
    String actionType() default "none";
}

