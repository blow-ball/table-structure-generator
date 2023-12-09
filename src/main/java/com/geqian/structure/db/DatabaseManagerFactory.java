package com.geqian.structure.db;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author geqian
 * @date 16:12 2023/9/11
 */
@Component
public class DatabaseManagerFactory {

    private static Map<String, DatabaseManager> databaseManagerMap;

    public DatabaseManagerFactory(Map<String, DatabaseManager> databaseManagerMap) {
        DatabaseManagerFactory.databaseManagerMap = databaseManagerMap;
    }

    /**
     * 根据数据库，获取对象的数据库信息
     * @param databaseType
     * @return
     */
    public static DatabaseManager getDatabaseManager(String databaseType) {
        return DatabaseManagerFactory.databaseManagerMap.get(databaseType);
    }
}
