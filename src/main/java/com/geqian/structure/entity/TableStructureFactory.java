package com.geqian.structure.entity;

import com.geqian.structure.utils.PackageScanner;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author geqian
 * @date 3:13 2023/12/8
 */
public class TableStructureFactory {

    private static Map<String, Supplier<Class<? extends TableStructure>>> tableStructureMap;

    static {
        init();
    }


    private static void init() {

        PackageScanner packageScanner = new PackageScanner();

        Predicate<String> typeFilter = className -> {
            try {
                return Class.forName(className).getSuperclass() == TableStructure.class;
            } catch (ClassNotFoundException e) {
                return false;
            }
        };
        List<String> classes = packageScanner.scanPackage("com.geqian.structure.entity", typeFilter);
        if (!CollectionUtils.isEmpty(classes)) {
            tableStructureMap = new HashMap<>();
            for (String className : classes) {
                try {
                    Class<? extends TableStructure> type = (Class<? extends TableStructure>) Class.forName(className);
                    String key = type.getSimpleName().replace("TableStructure", "").toLowerCase();
                    tableStructureMap.put(key, () -> type);
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
    }

    /**
     * 创建一个列信息存储容器
     *
     * @param dbType
     * @return
     */
    public static Class<? extends TableStructure> getTableStructureType(String dbType) {
        Class<? extends TableStructure> type = tableStructureMap.get(dbType.toLowerCase()).get();
        return type == null ? tableStructureMap.get("default").get() : type;
    }


}
