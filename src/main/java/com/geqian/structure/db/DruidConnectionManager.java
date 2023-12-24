package com.geqian.structure.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.geqian.structure.common.dto.DataSourceDto;

import java.sql.Connection;
import java.util.Properties;

/**
 * @author geqian
 * @date 20:16 2023/1/4
 */
public class DruidConnectionManager {

    private static volatile DruidDataSource dataSource;

    private static DataSourceDto dataSourceDto;


    public static void setDataSource(DataSourceDto dataSource) {
        DruidConnectionManager.dataSourceDto = dataSource;
    }

    public static void initDataSource() {
        try {
            DatabaseManager databaseManager = DatabaseManagerFactory.getDatabaseManager(dataSourceDto.getDatabaseType());
            Class.forName(databaseManager.getDriverClass());
            String url = databaseManager.getUrl(dataSourceDto.getDatabase(), dataSourceDto.getIp(), dataSourceDto.getPort());
            Properties properties = new Properties();
            String threadSize = String.valueOf(Runtime.getRuntime().availableProcessors() + 1);
            //初始化连接数量
            properties.setProperty("initialSize", threadSize);
            //最大连接数
            properties.setProperty("maxActive", threadSize);
            //最大等待时间
            properties.setProperty("maxWait", "30000");
            properties.setProperty("driverClassName", databaseManager.getDriverClass());
            properties.setProperty("url", url);
            properties.setProperty("username", dataSourceDto.getUsername());
            properties.setProperty("password", dataSourceDto.getPassword());
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
            // 失败后重连的次数
            dataSource.setConnectionErrorRetryAttempts(0);
            //请求失败之后中断
            dataSource.setBreakAfterAcquireFailure(true);
            CurrentDatabaseManager.setDatabaseManager(databaseManager);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("初始化数据源失败," + e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            if (dataSource == null) {
                synchronized (DruidConnectionManager.class) {
                    if (dataSource == null) {
                        initDataSource();
                    }
                }
            }
            return dataSource.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("获取连接失败," + e.getMessage());
        }
    }


    public static DataSourceDto getDataSource() {
        return dataSourceDto;
    }

    public static void clearDatasource() {
        DruidConnectionManager.dataSource = null;
    }
}
