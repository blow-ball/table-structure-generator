package com.geqian.structure.entity;

import com.geqian.document4j.common.annotation.TableField;

/**
 * 列字段信息
 *
 * @author geqian
 * @date 10:45 2023/1/5
 */

public class H2TableStructure extends TableStructure {

    @TableField(value = "长度", order = 7, exclude = true, enums = "null-> ")
    private Long length;

    public void setLength(Long length) {
        this.length = length;
    }

    public Long getLength() {
        return length;
    }
}
