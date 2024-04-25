package com.geqian.structure.jdbc;

import java.lang.annotation.*;

/**
 * @author geqian
 * @date 1:49 2023/12/24
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Columns {

    Column[] value();

}
