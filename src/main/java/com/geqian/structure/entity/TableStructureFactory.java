package com.geqian.structure.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author geqian
 * @date 3:13 2023/12/8
 */
public class TableStructureFactory {

    private static final Map<String, Supplier<Class<? extends TableStructure>>> tableStructureMap;

    static {
        tableStructureMap = new HashMap<>();
        tableStructureMap.put("mysql", () -> MySQLTableStructure.class);
        tableStructureMap.put("oracle", () -> OracleTableStructure.class);
        tableStructureMap.put("db2", () -> DB2TableStructure.class);
        tableStructureMap.put("postgresql", () -> PostgreSqlTableStructure.class);
    }

    /**
     * 创建一个列信息存储容器
     *
     * @param dbType
     * @return
     */
    public static Class<? extends TableStructure> getTableStructureType(String dbType) {
        return tableStructureMap.get(dbType).get();
    }

}
