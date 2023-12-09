package com.geqian.structure.db;

/**
 * @author geqian
 * @date 21:32 2023/11/30
 */
public class OracleColumnNameDefinition implements ColumnNameDefinition {
    @Override
    public String getSchemaName() {
        return "USERNAME";
    }

    @Override
    public String getTableName() {
        return "TABLE_NAME";
    }

    @Override
    public String getTableComment() {
        return "COMMENTS";
    }

    @Override
    public String getColumnName() {
        return "COLUMN_NAME";
    }

    @Override
    public String getColumnType() {
        return "DATA_TYPE";
    }

    @Override
    public String getColumnComment() {
        return "COMMENTS";
    }

    @Override
    public String getIsNullable() {
        return "NULLABLE";
    }

    @Override
    public String getColumnDefault() {
        return "DATA_DEFAULT";
    }
}
