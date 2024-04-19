package com.geqian.structure.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ClassResourceScanner {

    /**
     * @param packageName 扫描的包
     * @param typeFilter  过滤器，参数说明：①包路径 ②文件名称
     * @return
     */
    public List<ClassResourceWrapper> doScan(String packageName, Predicate<ClassResourceWrapper> typeFilter) {
        return doScan(Collections.singletonList(packageName), typeFilter);
    }


    /**
     * @param packageNames 扫描的包
     * @param typeFilter   过滤器，参数说明：①包路径 ②文件名称
     * @return
     */
    public List<ClassResourceWrapper> doScan(List<String> packageNames, Predicate<ClassResourceWrapper> typeFilter) {
        List<ClassResourceWrapper> wrappers = new ArrayList<>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        for (String packageName : packageNames) {
            if (StringUtils.hasText(packageName)) {
                //将包名转换为资源路径
                String packagePath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(packageName) + "/" + "**/*.class";
                try {
                    Resource[] resources = resolver.getResources(packagePath);
                    for (Resource resource : resources) {
                        if (resource != null && resource.isReadable()) {
                            ClassResourceWrapper wrapper = new ClassResourceWrapper(packageName, resource);
                            if (typeFilter == null || typeFilter.test(wrapper)) {
                                wrappers.add(wrapper);
                            }
                        }
                    }
                } catch (IOException e) {
                    return Collections.emptyList();
                }
            }
        }
        return wrappers;
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


    /**
     * 文件元信息
     */
    public static class ClassResourceWrapper {

        private final String packageName;

        private Class<?> type;

        private final Resource resource;


        private ClassResourceWrapper(String packageName, Resource resource) {
            this.packageName = packageName;
            this.resource = resource;
            loadClass(resource);
        }

        public String getPackageName() {
            return packageName;
        }

        public Resource getResource() {
            return resource;
        }

        public Class<?> getType() {
            return type;
        }

        public boolean isInterface() {
            return type != null && type.isInterface() && !type.isAnnotation();
        }

        public boolean isAnnotation() {
            return type != null && type.isAnnotation();
        }

        public boolean isEnum() {
            return type != null && type.isEnum();
        }

        public boolean isClass() {
            return type != null && !type.isEnum() && !type.isInterface() && !type.isAnnotation();
        }

        public boolean isMemberClass() {
            return type != null && type.isMemberClass();
        }

        public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
            return type != null && type.isAnnotationPresent(annotation);
        }

        /**
         * 反射加载 class
         *
         * @param resource
         */
        private void loadClass(Resource resource) {
            if (resource != null) {
                try {
                    String classpath = Objects.requireNonNull(this.getClass().getResource("/")).getPath();
                    String path = resource.getURL().getPath().substring(classpath.length()).replace("/", ".");
                    String fullClassName = path.substring(0, path.length() - 6);
                    this.type = Class.forName(fullClassName);
                } catch (Exception ignore) {
                }
            }
        }
    }

}
