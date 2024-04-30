package com.geqian.structure.entity;

import com.geqian.document4j.common.annotation.TableField;

/**
 * @author geqian
 * @date 15:59 2024/3/7
 */
public class SQLite3TableStructure extends TableStructure {

    @TableField(value = "键", order = 6, exclude = true, enums = {"PRI->主键", "UNI->唯一键"})
    private String columnKey;

    public String getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }
}
