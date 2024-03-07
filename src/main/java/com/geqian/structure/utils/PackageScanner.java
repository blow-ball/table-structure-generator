package com.geqian.structure.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
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
     * @param typeFilter   过滤器
     * @return
     */
    public List<String> scanPackage(List<String> packageNames, Predicate<String> typeFilter) {
        // 获取当前线程的 classLoader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        List<String> classNames = new ArrayList<>();
        for (String packageName : packageNames) {
            // 将包名转换为文件路径
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources;
            try {
                // 获取 path 对应的所有资源的 URL
                resources = classLoader.getResources(path);
            } catch (IOException e) {
                return Collections.emptyList();
            }
            List<File> directories = new ArrayList<>();
            // 枚举 URL 集合的元素，将 URL 转换为文件，并添加到 directories 集合中
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                directories.add(new File(resource.getFile()));
            }
            // 遍历 dirs 中的文件夹和类文件
            for (File directory : directories) {
                classNames.addAll(findClasses(packageName, directory, typeFilter));
            }
        }
        return classNames;
    }


    /**
     * 扫描指定文件夹下的类文件，返回类名列表
     *
     * @param packageName
     * @param directory
     * @return
     */
    private List<String> findClasses(String packageName, File directory, Predicate<String> typeFilter) {
        List<String> classNames = new ArrayList<>();
        if (!directory.exists()) {
            return classNames;
        }
        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是文件夹，则递归查找其中的类文件
                    classNames.addAll(findClasses(packageName + "." + file.getName(), file, typeFilter));
                } else if (file.getName().endsWith(".class")) {
                    int indexOf = file.getName().length() - 6;
                    // 如果是类文件，则获取完整类名（包含包名）
                    String className = packageName + '.' + file.getName().substring(0, indexOf);
                    if (typeFilter == null || typeFilter.test(className)) {
                        classNames.add(className);
                    }
                }
            }
        }
        return classNames;
    }
}