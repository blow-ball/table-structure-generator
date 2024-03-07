package com.geqian.structure.db;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author geqian
 * @date 16:12 2023/9/11
 */

public class DatabaseManagerFactory {

    private final static Map<String, DatabaseManager> databaseManagerMap = new HashMap<>();

    static {
        init();
    }

    private static void init() {

        Yaml yaml = new Yaml();

        InputStream inputStream = DatabaseManagerFactory.class.getClassLoader().getResourceAsStream("application.yml");

        Map<String, Object> map = yaml.load(inputStream);

        Map<String, Map<String, Map>> databaseConfig = (Map<String, Map<String, Map>>) map.get("database");

        Map<String, Map> databaseMap = databaseConfig.get("info");

        Set<String> keySet = databaseMap.keySet();

        for (String key : keySet) {
            Map<String, String> database = databaseMap.get(key);
            DatabaseManager databaseManager = new DatabaseManager();
            databaseManager.setDriverClass(database.get("driverClass"));
            databaseManager.setUrl(database.get("url"));
            databaseManager.setDatabases(database.get("databases"));
            databaseManager.setTables(database.get("tables"));
            databaseManager.setTableInfo(database.get("tableInfo"));
            databaseManager.setTableStructure(database.get("tableStructure"));
            registerDatabaseManager(key.toLowerCase(), databaseManager);
        }
    }

    /**
     * 注册 DatabaseManager
     *
     * @param key
     * @param databaseManager
     */
    private static void registerDatabaseManager(String key, DatabaseManager databaseManager) {
        DatabaseManagerFactory.databaseManagerMap.put(key, databaseManager);
    }

    /**
     * 根据数据库，获取对象的数据库信息
     *
     * @param databaseType
     * @return
     */
    public static DatabaseManager getDatabaseManager(String databaseType) {
        return DatabaseManagerFactory.databaseManagerMap.get(databaseType);
    }
}
