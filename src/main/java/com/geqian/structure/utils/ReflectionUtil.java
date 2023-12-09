package com.geqian.structure.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author geqian
 * @date 3:43 2023/12/8
 */
public class ReflectionUtil {


    public static List<Field> getFields(Class<?> classType) {
        return Stream.of(classType.getDeclaredFields()).collect(Collectors.toList());
    }


    public static List<Field> getSuperclassFields(Class<?> classType) {
        return Stream.of(classType.getSuperclass().getDeclaredFields()).collect(Collectors.toList());
    }


}
