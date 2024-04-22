package com.geqian.structure.db;

import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author geqian
 * @date 16:12 2023/9/11
 */

public class DatabaseMetadataFactory {

    private final static Map<String, DatabaseMetaData> databaseMetaDatas = new HashMap<>();

    static {
        init();
    }

    private static void init() {

        Map<String, Object> map = parseYml();

        Map<String, Map<String, Map>> databaseConfig = (Map<String, Map<String, Map>>) map.get("database");

        Map<String, Map> databaseMap = databaseConfig.get("info");

        Set<String> keySet = databaseMap.keySet();

        for (String key : keySet) {
            Map<String, String> database = databaseMap.get(key);
            DatabaseMetaData metaData = new DatabaseMetaData();
            metaData.setDriverClass(database.get("driverClass"));
            metaData.setUrl(database.get("url"));
            metaData.setDatabases(database.get("databases"));
            metaData.setTables(database.get("tables"));
            metaData.setTableInfo(database.get("tableInfo"));
            metaData.setTableStructure(database.get("tableStructure"));
            register(key.toLowerCase(), metaData);
        }
    }

    /**
     * 注册 DatabaseMetaData
     *
     * @param key
     * @param metadata
     */
    private static void register(String key, DatabaseMetaData metadata) {
        DatabaseMetadataFactory.databaseMetaDatas.put(key, metadata);
    }

    /**
     * 根据数据库，获取对象的数据库信息
     *
     * @param databaseType
     * @return
     */
    public static DatabaseMetaData getMetaData(String databaseType) {
        return DatabaseMetadataFactory.databaseMetaDatas.get(databaseType);
    }


    private static Map<String, Object> parseYml() {
        YamlMapFactoryBean yamlMapFactoryBean = new YamlMapFactoryBean();
        yamlMapFactoryBean.setResources(new ClassPathResource("application.yml"));
        return yamlMapFactoryBean.getObject();
    }
}
