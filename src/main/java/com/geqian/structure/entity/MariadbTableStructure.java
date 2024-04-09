package com.geqian.structure.entity;

import com.geqian.document4j.common.annotation.TableField;

/**
 * 列字段信息
 *
 * @author geqian
 * @date 10:45 2023/1/5
 */
public class MariadbTableStructure extends TableStructure {

    @TableField(value = "键类型", order = 6, exclude = true, enums = {"PRI->主键", "UNI->唯一键"})
    private String columnKey;

    @TableField(value = "长度", order = 7, exclude = true, enums = "null-> ")
    private Long length;

    @TableField(value = "额外", order = 8, exclude = true)
    private String extra;


    public String getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }


}
