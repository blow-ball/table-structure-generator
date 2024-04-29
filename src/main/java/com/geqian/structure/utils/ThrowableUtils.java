package com.geqian.structure.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author geqian
 * @date 20:32 2024/4/29
 */
public class ThrowableUtils {

    /**
     * 获取异常及其子异常集合
     *
     * @param source
     * @return
     */
    public static List<Throwable> getThrowables(Throwable source) {
        Throwable throwable = source;
        List<Throwable> throwables = new ArrayList<>();
        while (throwable != null) {
            throwables.add(throwable);
            throwable = throwable.getCause();
        }
        return throwables;
    }


    /**
     * 获取根异常
     *
     * @param source
     * @return
     */
    public static Throwable getRootCause(Throwable source) {
        Throwable throwable = source;
        while (throwable != null) {
            Throwable childThrowable = throwable.getCause();
            if (childThrowable == null) {
                return throwable;
            }
            throwable = childThrowable;
        }
        return null;
    }


    /**
     * 判断异常及其子异常是否包含指定异常
     *
     * @param source
     * @param throwableType
     * @return
     */
    public static boolean contains(Throwable source, Class<? extends Throwable> throwableType) {
        if (source != null) {
            return getThrowables(source).stream().anyMatch(throwable -> throwable.getClass() == throwableType);
        }
        return false;
    }


    /**
     * 判断异常及其子异常是否包含指定异常
     *
     * @param targetTypes
     * @return
     */
    @SafeVarargs
    public static boolean containsAnyOne(Throwable source, Class<? extends Throwable>... targetTypes) {
        if (targetTypes != null && targetTypes.length > 0) {
            List<Class<? extends Throwable>> types = Stream.of(targetTypes).collect(Collectors.toList());
            return getThrowables(source).stream().anyMatch(throwable -> types.contains(throwable.getClass()));
        }
        return false;
    }

}
