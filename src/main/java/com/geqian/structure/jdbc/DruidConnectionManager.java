package com.geqian.structure.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.geqian.structure.common.dto.ConnectionInfoDto;
import com.geqian.structure.db.CurrentDatabaseMetadataHolder;
import com.geqian.structure.db.DatabaseMetaData;
import com.geqian.structure.db.DatabaseMetadataFactory;

import java.sql.Connection;
import java.util.Properties;

/**
 * @author geqian
 * @date 20:16 2023/1/4
 */
public class DruidConnectionManager {

    private static volatile DruidDataSource dataSource;

    private static ConnectionInfoDto connectionInfo;


    public static void setConnectionInfo(ConnectionInfoDto connectionInfoDto) {
        DruidConnectionManager.connectionInfo = connectionInfoDto;
    }

    public static void initDataSource() {
        try {
            DatabaseMetaData metadata = DatabaseMetadataFactory.getMetaData(connectionInfo.getDatabaseType());
            Class.forName(metadata.getDriverClass());
            String url = metadata.getUrl(connectionInfo.getDatabase(), connectionInfo.getIp(), connectionInfo.getPort());
            Properties properties = new Properties();
            String threadSize = String.valueOf(Runtime.getRuntime().availableProcessors() + 1);
            //初始化连接数量
            properties.setProperty("initialSize", threadSize);
            //最大连接数
            properties.setProperty("maxActive", threadSize);
            //最大等待时间
            properties.setProperty("maxWait", "30000");
            properties.setProperty("driverClassName", metadata.getDriverClass());
            properties.setProperty("url", url);
            properties.setProperty("username", connectionInfo.getUsername());
            properties.setProperty("password", connectionInfo.getPassword());
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
            // 失败后重连的次数
            dataSource.setConnectionErrorRetryAttempts(0);
            //请求失败之后中断
            dataSource.setBreakAfterAcquireFailure(true);
            CurrentDatabaseMetadataHolder.setMetaData(metadata);
        } catch (Exception e) {
            throw new RuntimeException("初始化数据源失败," + e);
        }
    }

    public static Connection getConnection() {
        if (dataSource == null) {
            synchronized (DruidConnectionManager.class) {
                if (dataSource == null) {
                    initDataSource();
                }
            }
        }
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("获取连接失败," + e.getMessage());
        }
    }


    public static ConnectionInfoDto getConnectionInfo() {
        return connectionInfo;
    }

    public static void clearDatasource() {
        DruidConnectionManager.dataSource = null;
    }
}
