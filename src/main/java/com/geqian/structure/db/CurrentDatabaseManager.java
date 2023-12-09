package com.geqian.structure.db;

/**
 * 当前数据库管理器
 *
 * @author geqian
 * @date 16:00 2023/11/11
 */
public class CurrentDatabaseManager {

    private static DatabaseManager databaseManager;

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static void setDatabaseManager(DatabaseManager databaseManager) {
        CurrentDatabaseManager.databaseManager = databaseManager;
    }
}
