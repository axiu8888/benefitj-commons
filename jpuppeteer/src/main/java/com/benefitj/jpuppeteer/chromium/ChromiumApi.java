package com.benefitj.jpuppeteer.chromium;

import java.lang.annotation.Target;
import java.lang.annotation.*;

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
