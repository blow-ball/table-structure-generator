package com.geqian.structure.jdbc;

import java.sql.ResultSet;

/**
 * @author geqian
 * @date 22:38 2024/4/19
 */
public interface ResultSetInterceptor<T> extends JDBCInterceptor {

    boolean support(String sql);

    T intercept(ResultSet resultSet);

}
