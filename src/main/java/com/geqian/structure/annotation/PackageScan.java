package com.geqian.structure.annotation;

import java.lang.annotation.*;

/**
 * @author geqian
 * @date 13:49 2024/4/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PackageScan {

    String value();

}
