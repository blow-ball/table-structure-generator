package com.geqian.structure.db;

/**
 * @author geqian
 * @date 21:32 2023/11/30
 */
public class DB2ColumnNameDefinition implements ColumnNameDefinition {
    @Override
    public String getSchemaName() {
        return "SCHEMANAME";
    }

    @Override
    public String getTableName() {
        return "TABNAME";
    }

    @Override
    public String getTableComment() {
        return "REMARKS";
    }

    @Override
    public String getColumnName() {
        return "COLNAME";
    }

    @Override
    public String getColumnType() {
        return "TYPENAME";
    }

    @Override
    public String getColumnComment() {
        return "REMARKS";
    }

    @Override
    public String getIsNullable() {
        return "NULLS";
    }

    @Override
    public String getColumnDefault() {
        return "DEFAULT";
    }
}
