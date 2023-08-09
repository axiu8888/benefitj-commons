package com.benefitj.jpuppeteer.chromium;

import java.lang.annotation.*;
import java.lang.annotation.Target;

/**
 * 接口
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ChromiumApi {

  String value();

}
