package com.geqian.structure.entity;

import com.geqian.structure.db.OracleColumnNameDefinition;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 列字段信息
 *
 * @author geqian
 * @date 10:45 2023/1/5
 */

public class OracleColumnContainer extends AbstractColumnContainer {

    public OracleColumnContainer() {
        super(new OracleColumnNameDefinition());
    }

    @Override
    public Map<String, Field> getColumnFieldMapping() {
        return super.getColumnFieldMapping();
    }
}
