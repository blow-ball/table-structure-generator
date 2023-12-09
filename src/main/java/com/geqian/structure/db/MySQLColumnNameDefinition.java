package com.geqian.structure.db;

/**
 * @author geqian
 * @date 21:31 2023/11/30
 */
public class MySQLColumnNameDefinition implements ColumnNameDefinition {

    @Override
    public String getSchemaName() {
        return "schema_name";
    }

    @Override
    public String getTableName() {
        return "table_name";
    }

    @Override
    public String getTableComment() {
        return "table_comment";
    }

    @Override
    public String getColumnName() {
        return "column_name";
    }

    @Override
    public String getColumnType() {
        return "column_type";
    }

    @Override
    public String getColumnComment() {
        return "column_comment";
    }

    @Override
    public String getIsNullable() {
        return "is_nullable";
    }

    @Override
    public String getColumnDefault() {
        return "column_default";
    }


    public String getColumnKey() {
        return "column_key";
    }


    public String getExtra() {
        return "extra";
    }


    public String getLength() {
        return "character_maximum_length";
    }



}
