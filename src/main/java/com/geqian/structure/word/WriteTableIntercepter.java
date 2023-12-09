package com.geqian.structure.word;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author geqian
 * @date 21:29 2023/11/5
 */
public interface WriteTableIntercepter extends WriteTableable {

    /**
     * 拦截写入表格的属性集合
     *
     * @param objectType
     * @param fields
     * @return
     */
    default List<Field> interceptWriteFields(Class<?> objectType, List<Field> fields) {
        return fields;
    }

    /**
     * 拦截写入表格的表头
     *
     * @param objectType
     * @param headers
     * @return
     */
    default List<String> interceptWriteHeaders(Class<?> objectType, List<Field> fields, List<String> headers) {
        return headers;
    }


    /**
     * 拦截写入表格单元格的属性值
     *
     * @param obj   当前拦截对象
     * @param field 当前拦截对象的属性
     * @return
     */
    default Object interceptWriteFieldValue(Object obj, Field field, Object fieldValue) {
        return fieldValue;
    }
}
