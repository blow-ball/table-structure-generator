package com.geqian.structure.interceptor;

import com.geqian.structure.entity.SQLite3TableStructure;
import com.geqian.structure.jdbc.ResultSetInterceptor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author geqian
 * @date 17:57 2024/4/30
 */
@Component
public class SQLite3TableInfoInterceptor implements ResultSetInterceptor<SQLite3TableStructure> {


    private final String tableInfo = "PRAGMA ${0}.table_info(${1})";

    @Override
    public boolean support(String productName, String sql) {
        return "sqlite".equalsIgnoreCase(productName) && sql.equalsIgnoreCase(tableInfo);
    }

    @Override
    public List<SQLite3TableStructure> intercept(ResultSet resultSet) throws Exception {
        List<SQLite3TableStructure> structures = new ArrayList<>();
        int columnCount = resultSet.getMetaData().getColumnCount();

        for (int index = 0; index < columnCount; index++) {
            String label = resultSet.getMetaData().getColumnLabel(index + 1);
            System.out.println(label);
        }
        while (resultSet.next()) {
            SQLite3TableStructure structure = new SQLite3TableStructure();
            String columnName = resultSet.getString("name");
            String columnType = resultSet.getString("type");
            int notnull = resultSet.getInt("notnull");
            String columnDefault = resultSet.getString("dflt_value");
            int pk = resultSet.getInt("pk");
            structure.setIsNullable(notnull == 0 ? "NO" : "YES");
            structure.setColumnType(columnType);
            structure.setColumnName(columnName);
            structure.setColumnDefault(columnDefault);
            structure.setColumnKey(pk == 1 ? "主键" : "");
            structures.add(structure);
        }
        return structures;
    }
}
