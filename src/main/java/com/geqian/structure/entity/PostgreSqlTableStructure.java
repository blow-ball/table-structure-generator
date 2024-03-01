package com.geqian.structure.entity;

import com.geqian.document4j.common.annotation.TableField;

/**
 * @author geqian
 * @date 17:16 2024/3/1
 */
public class PostgreSqlTableStructure extends TableStructure {

    @TableField(value = "长度", order = 7, exclude = true, enums = "null-> ")
    private Long length;

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }
}
