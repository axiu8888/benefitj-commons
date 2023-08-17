package com.benefitj.jpuppeteer;

import java.lang.annotation.*;

/**
 * 不等待响应
 *
 * @author Administrator
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE,
    ElementType.METHOD
})
@Inherited
public @interface NoAwait {
}
