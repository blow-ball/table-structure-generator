package com.geqian.structure.word;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordBytesBuilder {

    private final XWPFDocument document;

    private Map<String, String> enumMappings;

    private Map<String, Converter> converters;

    private WordBytesBuilder(XWPFDocument document) {
        this.document = document;
    }

    /**
     * 创建一个WordBuilder
     *
     * @return
     */
    public static WordBytesBuilder create() {
        return new WordBytesBuilder(new XWPFDocument());
    }

    /**
     * 换行
     *
     * @return
     */
    public WordBytesBuilder addCarriageReturn() {
        //创建段落
        document.createParagraph();
        // 对齐方式 paragraph.setAlignment(ParagraphAlignment.CENTER);
        return this;
    }


    /**
     * 添加一个新页面
     *
     * @return
     */
    public WordBytesBuilder addNewPage() {
        //创建段落
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.addBreak(BreakType.PAGE);
        return this;
    }


    /**
     * 添加段落文字
     *
     * @return
     */
    public WordBytesBuilder addTitle(String text) {
        addTitle(text, null);
        return this;
    }


    /**
     * 添加段落文本
     *
     * @return
     */
    public WordBytesBuilder addTitle(String text, ParagraphTextConfig config) {
        //创建段落
        XWPFParagraph paragraph = document.createParagraph();
        if (config == null) {
            config = new ParagraphTextConfig();
            config.setBlod(true);
            config.setAlignment(ParagraphAlignment.CENTER);
            config.setFontSize(25);
        }
        addParagraphText(paragraph, text, config);
        return this;
    }

    /**
     * 添加段落文字
     *
     * @return
     */
    public WordBytesBuilder addParagraphText(String text) {
        addParagraphText(text, new ParagraphTextConfig());
        return this;
    }


    /**
     * 添加段落文本
     *
     * @return
     */
    public WordBytesBuilder addParagraphText(String text, ParagraphTextConfig config) {
        //创建段落
        XWPFParagraph paragraph = document.createParagraph();
        addParagraphText(paragraph, text, config);
        return this;
    }

    /**
     * 添加段落文本
     *
     * @return
     */
    public void addParagraphText(XWPFParagraph paragraph, String text, ParagraphTextConfig config) {
        // 设置对齐方式 paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.setAlignment(config.getAlignment());
        //创建段落文本
        XWPFRun run = paragraph.createRun();
        // 下划线 run.setUnderline(UnderlinePatterns.DOT_DOT_DASH);
        //文本
        run.setText(text);
        // 粗体 run.setBold(true);
        run.setBold(config.isBlod());
        // 斜体 run.setItalic(true);
        run.setItalic(config.isItalic());
        //字体大小
        run.setFontSize(config.getFontSize());
        // 颜色 run.setColor("00ff00");
        run.setColor(config.getColor());
        // 字体 run.setFontFamily("Courier");
        run.setFontFamily(config.getFontFamily());
        //设置首行缩进
        setFirstLineIndent(paragraph, config.getFirstLineIndent());
        if (Objects.equals(true, config.isCarriageReturn())) {
            run.addBreak();
        }
    }

    /**
     * 设置段落首行缩进字符个数
     *
     * @param paragraph
     * @param indent
     */
    private void setFirstLineIndent(XWPFParagraph paragraph, int indent) {
        // 设置段落的首行缩进
        CTPPr ppr = paragraph.getCTP().getPPr();
        if (ppr == null) {
            ppr = paragraph.getCTP().addNewPPr();
        }
        CTInd ind = ppr.getInd();
        if (ind == null) {
            ind = ppr.addNewInd();
        }
        ind.setFirstLineChars(BigInteger.valueOf(100L * indent));
    }

    /**
     * 添加图片
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public WordBytesBuilder addPicture(byte[] bytes, String filename, int width, int height) {
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            return addPicture(is, filename, width, height);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 添加图片
     *
     * @param is
     * @return
     * @throws Exception
     */
    public WordBytesBuilder addPicture(InputStream is, String filename, int width, int height) {
        try {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, filename, Units.toEMU(width), Units.toEMU(height));
            return this;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加表格
     *
     * @param dataList 数据列表
     */
    public WordBytesBuilder addTable(List<? extends WriteTableable> dataList) {
        return addTable(dataList, new ParagraphTextConfig());
    }

    /**
     * 添加表格
     *
     * @param dataList 数据列表
     */
    public WordBytesBuilder addTable(List<? extends WriteTableable> dataList, ParagraphTextConfig config) {

        if (dataList != null && !(dataList = dataList.stream().filter(Objects::nonNull).collect(Collectors.toList())).isEmpty()) {

            WriteTableable writeTableable = dataList.get(0);

            NoHeaders noHeaders = writeTableable.getClass().getAnnotation(NoHeaders.class);

            //获取需要输出字段
            List<Field> fields = getFieldList(writeTableable);

            int rows = Objects.isNull(noHeaders) ? dataList.size() + 1 : dataList.size();

            int colums = fields.size();

            XWPFTable table = createTable(rows, colums);

            //加载枚举映射关系
            loadEnumMappings(fields);

            //加载全部属性值转换器
            loadConverters(fields);

            //表格内容开始行数
            int startRow = 0;

            if (Objects.isNull(noHeaders)) {
                //获取表头
                List<String> tableHeaders = getTableHeaders(fields);

                //写入表头
                writeTableHeaders(table, tableHeaders, writeTableable, fields, config);

                startRow = 1;
            }

            //写入表格主体数据
            writeTableRows(table, startRow, dataList, fields, config);
        }
        return this;
    }


    /**
     * 加载全部属性值转换器
     *
     * @param fields
     */
    private void loadConverters(List<Field> fields) {
        this.converters = new HashMap<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(TableField.class)) {
                TableField annotation = field.getAnnotation(TableField.class);
                if (annotation.converter() != NoConverter.class) {
                    try {
                        Converter converter = (Converter) annotation.converter().getConstructor().newInstance();
                        this.converters.put(getFieldKey(field), converter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 获取类名+属性名
     *
     * @param field
     * @return
     */
    private String getFieldKey(Field field) {
        return field.getDeclaringClass().getName() + "." + field.getName();
    }


    /**
     * 加载全部枚举映射关系
     *
     * @param fields
     * @return
     */
    private void loadEnumMappings(List<Field> fields) {
        this.enumMappings = new HashMap<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(TableField.class)) {
                TableField annotation = field.getAnnotation(TableField.class);
                String[] enums = annotation.enums();
                if (enums.length > 0) {
                    appendEnumMapping(getFieldKey(field), enums);
                }
            }
        }
    }


    /**
     * 追加一组枚举映射关系
     *
     * @param fieldKey
     * @param enums
     */
    private void appendEnumMapping(String fieldKey, String[] enums) {
        for (String enumStr : enums) {
            String[] splitArray = enumStr.split("->");
            if (splitArray.length != 2) {
                throw new IllegalArgumentException("The convert property format of TableField is：oldValue -> newValue");
            }
            this.enumMappings.put(fieldKey + ":" + splitArray[0].trim(), splitArray[1].trim());
        }
    }


    /**
     * 创建表格
     *
     * @param rows
     * @param columns
     * @return
     */
    private XWPFTable createTable(int rows, int columns) {
        //创建表格
        XWPFTable table = document.createTable(rows, columns);
        //设置表格样式
        setTableStyle(table);

        return table;
    }


    /**
     * 写入表头
     *
     * @param table
     * @param tableHeaders
     * @param config
     */
    private void writeTableHeaders(XWPFTable table, List<String> tableHeaders, WriteTableable obj, List<Field> fields, ParagraphTextConfig config) {
        List<String> interceptedHeaders = obj instanceof WriteTableIntercepter
                ? ((WriteTableIntercepter) obj).interceptWriteHeaders(obj.getClass(), fields, tableHeaders)
                : tableHeaders;
        for (int i = 0; i < interceptedHeaders.size(); i++) {
            XWPFParagraph paragraph = table.getRow(0).getCell(i).getParagraphs().get(0);
            addParagraphText(paragraph, interceptedHeaders.get(i), config);
        }
    }

    /**
     * 写入表格多行数据
     *
     * @param table
     * @param dataList
     * @param fields
     * @param config
     */
    private void writeTableRows(XWPFTable table, int startRow, List<? extends WriteTableable> dataList, List<Field> fields, ParagraphTextConfig config) {

        for (int i = 0; i < dataList.size(); i++) {
            writeTableRow(table, i + startRow, dataList.get(i), fields, config);
        }
    }


    /**
     * 写入表格一行数据
     *
     * @param table
     * @param rowIndex
     * @param obj
     * @param fieldList
     * @param config
     */
    private void writeTableRow(XWPFTable table, int rowIndex, WriteTableable obj, List<Field> fieldList, ParagraphTextConfig config) {
        for (int i = 0; i < fieldList.size(); i++) {
            XWPFParagraph paragraph = table.getRow(rowIndex).getCell(i).getParagraphs().get(0);
            String cellValue = getCellValue(obj, fieldList.get(i));
            addParagraphText(paragraph, cellValue, config);
        }
    }

    /**
     * 获取单元格值
     *
     * @param obj
     * @param field
     * @return
     */
    private String getCellValue(WriteTableable obj, Field field) {
        Object fieldValue;
        try {
            field.setAccessible(true);
            fieldValue = field.get(obj);

            //执行属性值枚举映射
            Object enumValue = this.enumMappings.get(getFieldKey(field) + ":" + fieldValue);
            if (enumValue != null) {
                fieldValue = enumValue;
            }

            //执行属性值转换
            Converter converter = this.converters.get(getFieldKey(field));
            if (converter != null) {
                fieldValue = converter.convert(fieldValue);
            }

            //执行属性值拦截
            if (obj instanceof WriteTableIntercepter) {
                fieldValue = ((WriteTableIntercepter) obj).interceptWriteFieldValue(obj, field, fieldValue);
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            fieldValue = "";
        }

        return String.valueOf(fieldValue);
    }

    /**
     * 数据写入表格
     *
     * @param tableRows 数据列表
     */
    public WordBytesBuilder writeTableRows(Object[][] tableRows) {
        return writeTableRows(tableRows, new ParagraphTextConfig());
    }


    /**
     * 数据写入表格
     *
     * @param rows 数据列表
     */
    public WordBytesBuilder writeTableRows(Object[][] rows, ParagraphTextConfig config) {
        int row;
        int column;
        if (rows != null && (rows = Stream.of(rows).filter(Objects::nonNull).toArray(Object[][]::new)).length > 0) {
            //有效行数
            row = rows.length;
            Optional<Object[]> optional = Stream.of(rows).max(Comparator.comparingInt(array -> array == null ? 0 : array.length));
            if (optional.isPresent()) {
                //子数组长度最大的长度作为列数
                column = optional.get().length;
                //创建表格
                XWPFTable table = createTable(row, column);
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < rows[i].length; j++) {
                        XWPFParagraph paragraph = table.getRow(i).getCell(j).getParagraphs().get(0);
                        addParagraphText(paragraph, String.valueOf(rows[i][j]), config);
                    }
                }
            }
        }
        return this;
    }


    /**
     * 设置表格样式
     *
     * @param table
     */
    private void setTableStyle(XWPFTable table) {
        // 设置表格对齐方式
        table.setCellMargins(70, 0, 70, 0);
        table.setTableAlignment(TableRowAlign.LEFT);
        // 设置表格样式
        table.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf(8000));
        table.getCTTbl().getTblPr().unsetTblBorders();

        // 设置表格边框线条类型
        CTTblBorders borders = table.getCTTbl().addNewTblPr().addNewTblBorders();
        borders.addNewTop().setVal(STBorder.SINGLE);
        borders.addNewBottom().setVal(STBorder.SINGLE);
        borders.addNewLeft().setVal(STBorder.SINGLE);
        borders.addNewRight().setVal(STBorder.SINGLE);
        borders.addNewInsideH().setVal(STBorder.SINGLE);
        borders.addNewInsideV().setVal(STBorder.SINGLE);
    }

    /**
     * 获取表头
     *
     * @param fieldList
     * @return
     */
    private List<String> getTableHeaders(List<Field> fieldList) {
        //通过注解获取需要输出的属性别名
        return fieldList.stream()
                .map(field -> {
                    TableField annotation = field.getAnnotation(TableField.class);
                    return annotation != null && !Objects.equals("", annotation.value())
                            ? annotation.value()
                            : field.getName();
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取需要写入表格的属性
     *
     * @param obj
     * @return
     */
    private <T extends WriteTableable> List<Field> getFieldList(T obj) {

        List<Field> fields = new ArrayList<>();

        Class<?> classType = obj.getClass();

        while (!Objects.equals(classType, Object.class)) {
            List<Field> fieldList = Stream.of(classType.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(TableField.class) && !field.getAnnotation(TableField.class).exclude())
                    .collect(Collectors.toList());
            fields.addAll(fieldList);
            classType = classType.getSuperclass();
        }

        if (obj instanceof WriteTableIntercepter) {
            fields = ((WriteTableIntercepter) obj).interceptWriteFields(obj.getClass(), fields);
        }

        return fields.stream().sorted((a, b) -> {
            TableField before = a.getAnnotation(TableField.class);
            TableField next = b.getAnnotation(TableField.class);
            return (before == null ? Integer.MAX_VALUE : before.order()) - (next == null ? Integer.MAX_VALUE : next.order());
        }).collect(Collectors.toList());
    }


    /**
     * 构建 word字节数组
     *
     * @return
     * @throws Exception
     */
    public byte[] build() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            document.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭word文档对象
            closeDocument();
        }
    }


    /**
     * 关闭word文档对象
     */
    private void closeDocument() {
        if (document != null) {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}