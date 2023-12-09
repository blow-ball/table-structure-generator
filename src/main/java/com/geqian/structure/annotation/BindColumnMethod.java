package com.geqian.structure.annotation;

import java.lang.annotation.*;

/**
 * @author geqian
 * @date 22:05 2023/12/8
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BindColumnMethod {

    String value();

}
