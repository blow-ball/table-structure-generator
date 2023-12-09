package com.geqian.structure.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author geqian
 * @date 3:13 2023/12/8
 */
public class ColumnContainerFactory {

    private static final Map<String, Supplier<AbstractColumnContainer>> columnContainerMap;

    static {
        columnContainerMap = new HashMap<>();
        columnContainerMap.put("mysql", MySQLColumnContainer::new);
        columnContainerMap.put("oracle", OracleColumnContainer::new);
        columnContainerMap.put("db2", DB2ColumnContainer::new);
    }

    /**
     * 创建一个列信息存储容器
     *
     * @param dbType
     * @return
     */
    public static AbstractColumnContainer getColumnContainer(String dbType) {
        return columnContainerMap.get(dbType).get();
    }


    /**
     * 创建一个列信息存储容器
     *
     * @param dbType
     * @return
     */
    public static <T> T getColumnContainer(String dbType, Class<T> containerType) {
        return (T) columnContainerMap.get(dbType);
    }

}
