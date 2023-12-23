package com.geqian.structure.entity;

import com.geqian.structure.annotation.BindColumnMethod;
import com.geqian.structure.word.TableField;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 列字段信息
 *
 * @author geqian
 * @date 10:45 2023/1/5
 */
public class MySQLColumnContainer extends AbstractColumnContainer {

    @BindColumnMethod("getColumnKey")
    @TableField(value = "键类型", order = 6, exclude = true, enums = {"PRI->主键", "UNI->唯一键"})
    private String columnKey;

    @BindColumnMethod("getLength")
    @TableField(value = "长度", order = 7, exclude = true, enums = "null-> ")
    private String length;

    @BindColumnMethod("getExtra")
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

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    @Override
    public List<Field> getFields() {
        return super.getFields();
    }

}
