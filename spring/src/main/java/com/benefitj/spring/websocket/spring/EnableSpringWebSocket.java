package com.benefitj.spring.websocket.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Spring websocket
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SpringServerEndpointConfiguration.class)
public @interface EnableSpringWebSocket {
}
