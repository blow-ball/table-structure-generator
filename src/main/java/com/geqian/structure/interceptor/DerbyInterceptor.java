package com.geqian.structure.interceptor;

import com.geqian.structure.entity.DerbyTableStructure;
import com.geqian.structure.entity.TableStructure;
import com.geqian.structure.jdbc.ResultSetInterceptor;
import org.apache.derby.catalog.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author geqian
 * @date 18:02 2024/4/29
 */
@Component
public class DerbyInterceptor implements ResultSetInterceptor<TableStructure> {

    private final String targetSql = "SELECT c.COLUMNNAME as \"columnName\", c.COLUMNDATATYPE as \"columnType\", c.COLUMNDEFAULT as \"columnDefault\"\n" +
            "        FROM SYS.SYSTABLES t, SYS.SYSCOLUMNS c\n" +
            "        WHERE c.REFERENCEID = t.TABLEID AND t.TABLENAME = #{1} ORDER BY c.COLUMNNUMBER";

    @Override
    public boolean support(String productName, String sql) {
        return "apache derby".equalsIgnoreCase(productName) && formatSql(targetSql).equals(sql);
    }

    @Override
    public List<TableStructure> intercept(ResultSet resultSet) throws Exception {
        List<TableStructure> structures = new ArrayList<>();
        while (resultSet.next()) {
            String columnName = resultSet.getString("columNname");
            String columnDefault = resultSet.getString("columnDefault");
            if (columnDefault != null && columnDefault.matches("^'.*'$")) {
                columnDefault = columnDefault.substring(1, columnDefault.length() - 1);
            }
            TypeDescriptor typeDescriptor = (TypeDescriptor) resultSet.getObject("columnType");
            DerbyTableStructure tableStructure = new DerbyTableStructure();
            tableStructure.setColumnName(columnName);
            tableStructure.setColumnType(typeDescriptor.getTypeName());
            tableStructure.setIsNullable(typeDescriptor.isNullable() ? "YES" : "No");
            tableStructure.setLength((long) typeDescriptor.getMaximumWidth());
            tableStructure.setColumnDefault(columnDefault);
            structures.add(tableStructure);
        }
        return structures;
    }
}
