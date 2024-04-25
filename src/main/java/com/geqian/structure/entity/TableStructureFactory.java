package com.geqian.structure.entity;

import com.geqian.structure.annotation.PackageScan;
import com.geqian.structure.utils.ClassResourceScanner;
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
@PackageScan("com.geqian.structure.entity")
public class TableStructureFactory {

    private static Map<String, Supplier<Class<? extends TableStructure>>> tableStructureMap;

    static {
        init();
    }


    private static void init() {

        ClassResourceScanner classScanner = new ClassResourceScanner();

        Predicate<ClassResourceScanner.ClassResourceWrapper> typeFilter = wrapper -> wrapper.isClass() && TableStructure.class.isAssignableFrom(wrapper.getType());

        PackageScan packageScan = TableStructureFactory.class.getAnnotation(PackageScan.class);

        List<ClassResourceScanner.ClassResourceWrapper> wrappers = classScanner.doScan(packageScan.value(), typeFilter);

        if (!CollectionUtils.isEmpty(wrappers)) {
            tableStructureMap = new HashMap<>();
            for (ClassResourceScanner.ClassResourceWrapper wrapper : wrappers) {
                Class<? extends TableStructure> type = (Class<? extends TableStructure>) wrapper.getType();
                String key = type.getSimpleName().replace("TableStructure", "").toLowerCase();
                tableStructureMap.put(key, () -> type);
            }
        }
    }

    /**
     * 通过数据库类型，获取实体类class对象
     *
     * @param dbType
     * @return
     */
    public static Class<? extends TableStructure> getTableStructureType(String dbType) {
        Supplier<Class<? extends TableStructure>> supplier = tableStructureMap.get(dbType.toLowerCase());
        return supplier == null ? tableStructureMap.get("default").get() : supplier.get();
    }


}
