package com.cydeo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)     //means; this annotation can be used at method level
@Retention(RetentionPolicy.RUNTIME)     //will be active runtime
public @interface DefaultExceptionMessage {

    String defaultMessage() default "";
}
