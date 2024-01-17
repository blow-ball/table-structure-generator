package com.geqian.structure.entity;

import com.geqian.structure.db.DefaultColumnManager;
import com.geqian.structure.pdf.PDFWriteTableIntercepter;
import com.geqian.structure.pdf.PdfTableField;
import com.geqian.structure.word.WordTableField;
import com.geqian.structure.word.WordWriteTableIntercepter;
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
public abstract class TableStructure implements WordWriteTableIntercepter, PDFWriteTableIntercepter {

    @WordTableField(value = "序号", order = 0)
    @PdfTableField(value = "序号", order = 0)
    private Integer number;

    @WordTableField(value = "列名", order = 2)
    @PdfTableField(value = "列名", order = 2)
    private String columnName;

    @WordTableField(value = "数据类型", order = 3)
    @PdfTableField(value = "数据类型", order = 3)
    private String columnType;

    @WordTableField(value = "是否能为空", order = 4, exclude = true)
    @PdfTableField(value = "是否能为空", order = 4, exclude = true)
    private String isNullable;

    @WordTableField(value = "默认值", order = 5, enums = "null->NULL", exclude = true)
    @PdfTableField(value = "默认值", order = 5, enums = "null->NULL", exclude = true)
    private String columnDefault;

    @WordTableField(value = "备注", order = 6)
    @PdfTableField(value = "备注", order = 6)
    private String columnComment;


    @Override
    public List<Field> interceptWriteFields(Class<?> objectType, List<Field> fields) {

        List<Class<?>> classes = new ArrayList<>();

        Class<?> classType = objectType;

        while (!Objects.equals(classType, Object.class)) {
            classes.add(classType);
            classType = classType.getSuperclass();
        }

        return classes.stream()
                .map(pojoClass -> Stream.of(pojoClass.getDeclaredFields()).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .filter(field -> field.isAnnotationPresent(WordTableField.class) && DefaultColumnManager.getDefaultColumns().contains(field.getName()))
                .collect(Collectors.toList());
    }


    @Override
    public List<Field> interceptFields(Class<?> type, List<Field> fields) {
        List<Class<?>> classes = new ArrayList<>();

        Class<?> classType = type;

        while (!Objects.equals(classType, Object.class)) {
            classes.add(classType);
            classType = classType.getSuperclass();
        }

        return classes.stream()
                .map(pojoClass -> Stream.of(pojoClass.getDeclaredFields()).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .filter(field -> field.isAnnotationPresent(WordTableField.class) && DefaultColumnManager.getDefaultColumns().contains(field.getName()))
                .collect(Collectors.toList());
    }

}
