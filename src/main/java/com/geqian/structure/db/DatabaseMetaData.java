package com.geqian.structure.db;

import lombok.Data;

/**
 * 数据库连接信息、表结结构消息及相关查询sql语句
 *
 * @author geqian
 * @date 12:55 2023/7/12
 */
@Data
public class DatabaseMetaData {

    private String driverClass;
    private String url;
    private String databases;
    private String tables;
    private String tableInfo;
    private String tableStructure;


    public String getUrl(String database, String ip, int port) {
        return url.replace("${database}", database).replace("${ip}", ip).replace("${port}", String.valueOf(port));
    }

}
