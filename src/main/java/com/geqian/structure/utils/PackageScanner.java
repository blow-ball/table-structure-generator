package com.geqian.structure.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class PackageScanner {


    /**
     * @param packageName 扫描的包
     * @param typeFilter  过滤器
     * @return
     */
    public List<String> scanPackage(String packageName, Predicate<String> typeFilter) {
        return scanPackage(Collections.singletonList(packageName), typeFilter);
    }


    /**
     * @param packageNames 扫描的包
     * @param typeFilter  过滤器
     * @return
     */
    public List<String> scanPackage(List<String> packageNames, Predicate<String> typeFilter) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<String> classNames = new ArrayList<>();
        for (String packageName : packageNames) {
            //将包名转换为资源路径
            String packagePath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(packageName) + "/" + "**/*.class";
            try {
                Resource[] resources = resolver.getResources(packagePath);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        String filename = resource.getFilename();
                        String className = packageName + "." + filename.substring(0, filename.length() - 6);
                        if (typeFilter == null || typeFilter.test(className)) {
                            classNames.add(className);
                        }
                    }
                }
            } catch (IOException e) {
                return Collections.emptyList();
            }
        }
        return classNames;
    }


    /**
     * 解析基础包路径
     *
     * @param basePackage
     * @return
     */
    private static String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }

}