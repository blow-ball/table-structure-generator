package com.geqian.structure.db;

/**
 * 当前数据库元信息持有者
 *
 * @author geqian
 * @date 16:00 2023/11/11
 */
public class CurrentDatabaseMetadataHolder {

    private static DatabaseMetaData databaseMetadata;

    public static DatabaseMetaData getMetaData() {
        return databaseMetadata;
    }

    public static void setMetaData(DatabaseMetaData metadata) {
        CurrentDatabaseMetadataHolder.databaseMetadata = metadata;
    }
}
