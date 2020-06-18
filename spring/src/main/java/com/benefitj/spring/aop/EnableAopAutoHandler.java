package com.benefitj.spring.aop;

import com.benefitj.spring.aop.simple.SimpleAopConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * Aop 自动配置注解
 */
@Import({SimpleAopConfiguration.class})
@Lazy
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAopAutoHandler {
}
