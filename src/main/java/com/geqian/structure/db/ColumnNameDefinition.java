package com.geqian.structure.db;

/**
 * @author geqian
 * @date 21:28 2023/11/30
 */
public interface ColumnNameDefinition {

    /**
     * 查询Schema名称对应列名
     * @return
     */
    String getSchemaName();

    /**
     * 查询表名对于列名
     * @return
     */
    String getTableName();

    /**
     * 查询表注释对于列名
     * @return
     */
    String getTableComment();

    /**
     * 查询列名对于列名
     * @return
     */
    String getColumnName();

    /**
     * 查询列类型对于列名
     * @return
     */
    String getColumnType();

    /**
     * 查询列注释对于列名
     * @return
     */
    String getColumnComment();

    /**
     * 查询列是否可以为空对于列名
     * @return
     */
    String getIsNullable();

    /**
     * 查询列默认值对于列名
     * @return
     */
    String getColumnDefault();

}
