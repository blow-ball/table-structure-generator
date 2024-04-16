package com.geqian.structure.entity;

/**
 * 数据库表基本信息
 *
 * @author geqian
 * @date 21:13 2023/1/4
 */
public class TableDefinition {

    private String tableName;

    private String tableComment;

    public TableDefinition() {
    }

    public TableDefinition(String tableName, String tableComment) {
        this.tableName = tableName;
        this.tableComment = tableComment;
    }


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    @Override
    public String toString() {
        return "TableDefinition{" +
                ", tableName='" + tableName + '\'' +
                ", tableComment='" + tableComment + '\'' +
                '}';
    }
}
