package com.gs.rm.common.csv;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author: linjuntan
 * date: 2018/2/27
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvAlias {
    String alias() default "";

    Class clazz() default String.class;
}
