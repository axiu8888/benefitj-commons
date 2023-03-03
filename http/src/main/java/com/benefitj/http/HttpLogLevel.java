package com.benefitj.http;

import okhttp3.logging.HttpLoggingInterceptor;

import java.lang.annotation.*;

/**
 * 日志等级
 *
 * @author dxa
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface HttpLogLevel {

  HttpLoggingInterceptor.Level value() default HttpLoggingInterceptor.Level.NONE;

}
