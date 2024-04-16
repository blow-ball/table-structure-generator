package com.geqian.structure.entity;

import com.geqian.document4j.common.annotation.TableField;

/**
 * @author geqian
 * @date 16:41 2024/4/16
 */
public class Dm8TableStructure extends TableStructure{

    @TableField(value = "长度", order = 7, exclude = true, enums = "null-> ")
    private Long length;

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }
}
