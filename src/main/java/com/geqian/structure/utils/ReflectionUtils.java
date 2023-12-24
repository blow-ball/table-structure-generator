package com.geqian.structure.utils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectionUtils {


    /**
     * 获取Class对象及父类Class对象全部属性，并堆属性进行条件过滤
     *
     * @param classType
     * @param condition
     * @return
     */
    public static List<Field> getFieldAllContainSuperclass(Class<?> classType, Predicate<Field> condition) {

        List<Class<?>> classes = new ArrayList<>();

        //遍历获取父类class
        while (!Objects.equals(classType, Object.class)) {
            classes.add(classType);
            classType = classType.getSuperclass();
        }

        return classes.stream()
                .map(pojoClass -> Stream.of(pojoClass.getDeclaredFields()).filter(condition).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toList());
    }


    /**
     * 获取Class对象及父类Class对象全部属性，并堆属性进行条件过滤
     *
     * @param classType
     * @param condition
     * @return
     */
    public static Map<String, Field> getFieldMapContainSuperclass(Class<?> classType, Predicate<Field> condition) {

        List<Class<?>> classes = new ArrayList<>();

        //遍历获取父类class
        while (!Objects.equals(classType, Object.class)) {
            classes.add(classType);
            classType = classType.getSuperclass();
        }

        return classes.stream()
                .map(pojoClass -> Stream.of(pojoClass.getDeclaredFields()).filter(condition).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toMap(Field::getName, Function.identity(), (oldVal, newVal) -> oldVal));
    }

}