package com.geqian.structure.db;

import java.util.List;

/**
 * @author geqian
 * @date 22:02 2023/9/20
 */
public class DefaultColumnHolder {

    //保存不排除字段
    private static List<String> defaultColumns;

    public static List<String> getDefaultColumns() {
        return defaultColumns;
    }

    public static void setDefaultColumns(List<String> defaultColumns) {
        DefaultColumnHolder.defaultColumns = defaultColumns;
    }
}
