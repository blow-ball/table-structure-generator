package com.geqian.structure.jdbc;

import java.sql.ResultSet;
import java.util.List;

/**
 * @author geqian
 * @date 22:38 2024/4/19
 */
public interface ResultSetInterceptor<T> extends JDBCInterceptor {

    /**
     * @param productName 数据库产品名称
     * @param sql         执行的sql语句
     * @return
     */
    boolean support(String productName, String sql);

    /**
     * @param resultSet sql执行结果集
     * @return 最终查询结果
     */
    List<T> intercept(ResultSet resultSet);

}
