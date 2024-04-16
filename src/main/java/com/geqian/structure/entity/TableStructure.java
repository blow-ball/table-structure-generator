package com.geqian.structure.entity;

import com.geqian.document4j.common.annotation.TableField;
import com.geqian.document4j.html.HTMLTableInterceptor;
import com.geqian.document4j.md.MarkdownTableInterceptor;
import com.geqian.document4j.pdf.PDFTableInterceptor;
import com.geqian.document4j.word.WordTableInterceptor;
import com.geqian.structure.db.DefaultColumnManager;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 列字段信息
 *
 * @author geqian
 * @date 10:45 2023/1/5
 */
@Data
public abstract class TableStructure implements WordTableInterceptor, PDFTableInterceptor, MarkdownTableInterceptor, HTMLTableInterceptor {

    @TableField(value = "序号", order = 0)
    private Integer number;


    @TableField(value = "列名", order = 2)
    private String columnName;


    @TableField(value = "数据类型", order = 3)
    private String columnType;


    @TableField(value = "允许空值", order = 4, exclude = true, enums = {"Y->YES", "N->NO"})
    private String isNullable;


    @TableField(value = "默认值", order = 5, enums = "null->NULL", exclude = true)
    private String columnDefault;


    @TableField(value = "备注", order = 6, enums = "null-> ")
    private String columnComment;


    @Override
    public List<Field> interceptPdfFields(Class<?> type, List<Field> fields) {
        return getFields(type);
    }


    @Override
    public List<Field> interceptWordFields(Class<?> type, List<Field> fields) {
        return getFields(type);
    }


    @Override
    public List<Field> interceptMdFields(Class<?> type, List<Field> fields) {
        return getFields(type);
    }

    @Override
    public List<Field> interceptHtmlFields(List<Field> fields, Class<?> type) {
        return getFields(type);
    }

    private List<Field> getFields(Class<?> objectType) {

        List<Class<?>> classes = new ArrayList<>();

        Class<?> classType = objectType;

        while (!Objects.equals(classType, Object.class)) {
            classes.add(classType);
            classType = classType.getSuperclass();
        }

        return classes.stream()
                .map(pojoClass -> Stream.of(pojoClass.getDeclaredFields()).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .filter(field -> field.isAnnotationPresent(TableField.class) && DefaultColumnManager.getDefaultColumns().contains(field.getName()))
                .collect(Collectors.toList());
    }

}
