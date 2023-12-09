package com.geqian.structure.entity;

import com.geqian.structure.db.DB2ColumnNameDefinition;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 列字段信息
 *
 * @author geqian
 * @date 10:45 2023/1/5
 */

public class DB2ColumnContainer extends AbstractColumnContainer {


    public DB2ColumnContainer() {
        super(new DB2ColumnNameDefinition());
    }


    @Override
    public Map<String, Field> getColumnFieldMapping() {
        return super.getColumnFieldMapping();
    }
}
