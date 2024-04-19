package com.geqian.structure.service.impl;

import com.geqian.document4j.common.annotation.TableField;
import com.geqian.document4j.html.HTMLBuilder;
import com.geqian.document4j.html.HTMLStyle;
import com.geqian.document4j.md.MarkDownBuilder;
import com.geqian.document4j.md.MarkDownStyle;
import com.geqian.document4j.pdf.PDFBuilder;
import com.geqian.document4j.pdf.PdfStyle;
import com.geqian.document4j.word.WordBuilder;
import com.geqian.document4j.word.WordStyle;
import com.geqian.structure.bo.LabelAndValue;
import com.geqian.structure.bo.TableInfo;
import com.geqian.structure.common.ResponseResult;
import com.geqian.structure.common.dto.TableSelectDto;
import com.geqian.structure.common.dto.TargetTableDto;
import com.geqian.structure.common.vo.ColumnsVo;
import com.geqian.structure.db.DefaultColumnManager;
import com.geqian.structure.db.DruidConnectionManager;
import com.geqian.structure.entity.TableDefinition;
import com.geqian.structure.entity.TableStructure;
import com.geqian.structure.entity.TableStructureFactory;
import com.geqian.structure.entity.TreeNode;
import com.geqian.structure.mapper.TableMapper;
import com.geqian.structure.service.GeneratorService;
import com.geqian.structure.utils.ReflectionUtils;
import com.itextpdf.text.PageSize;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author geqian
 * @date 12:03 2023/7/12
 */
@Service
public class GeneratorServiceImpl implements GeneratorService {

    private final Logger log = LoggerFactory.getLogger(GeneratorServiceImpl.class);

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private TableMapper tableMapper;


    @SneakyThrows(Exception.class)
    @Override
    public void downloadPdf(TargetTableDto targetTableDto, HttpServletResponse response) {
        preview(targetTableDto, response);
    }

    @SneakyThrows(Exception.class)
    @Override
    public void preview(TargetTableDto targetTableDto, HttpServletResponse response) {
        byte[] pdfBytes = buildPdfDocument(targetTableDto);
        try (OutputStream out = response.getOutputStream()) {
            String filename = "表结构文档_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".pdf";
            //byte[] pdfBytes = WordToPdfUtils.word2007ToPdf(wordBytes);
            response.setHeader("content-type", "application/octet-stream");
            response.setHeader("filename", URLEncoder.encode(filename, "UTF-8"));
            //文件设置为附件
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            out.write(pdfBytes);
        }
    }

    @SneakyThrows(Exception.class)
    @Override
    public void downloadWord(TargetTableDto targetTableDto, HttpServletResponse response) {
        byte[] wordBytes = buildWordDocument(targetTableDto);
        try (OutputStream out = response.getOutputStream()) {
            String filename = "表结构文档_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".docx";
            response.setHeader("content-type", "application/octet-stream");
            response.setHeader("filename", URLEncoder.encode(filename, "UTF-8"));
            //文件设置为附件
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            out.write(wordBytes);
        }
    }

    @SneakyThrows(Exception.class)
    @Override
    public void downloadHtml(TargetTableDto targetTableDto, HttpServletResponse response) {
        byte[] htmlBytes = buildHtmlDocument(targetTableDto);
        try (OutputStream out = response.getOutputStream()) {
            String filename = "表结构文档_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".html";
            response.setHeader("content-type", "application/octet-stream");
            response.setHeader("filename", URLEncoder.encode(filename, "UTF-8"));
            //文件设置为附件
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));

            out.write(htmlBytes);
        }
    }


    @SneakyThrows(Exception.class)
    @Override
    public void downloadMarkdown(TargetTableDto targetTableDto, HttpServletResponse response) {
        try (OutputStream out = response.getOutputStream()) {
            byte[] mdBytes = buildMdDocument(targetTableDto);
            String filename = "表结构文档_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".md";
            response.setHeader("content-type", "application/octet-stream");
            response.setHeader("filename", URLEncoder.encode(filename, "UTF-8"));
            //文件设置为附件
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            out.write(mdBytes);
        }
    }

    @Override
    public ResponseResult<List<TreeNode>> getDatabases() {
        List<TreeNode> databases = tableMapper.getDatabases();
        return ResponseResult.success(databases);
    }

    @Override
    public ResponseResult<List<TreeNode>> getTables(TableSelectDto dto) {
        List<TreeNode> tables = tableMapper.getTables(dto.getSchemaName(), dto.getParentNodeId());
        return ResponseResult.success(tables);
    }


    @Override
    public ResponseResult<ColumnsVo> getTableColumnInfo() {

        ColumnsVo columnsVo = new ColumnsVo();

        String dbType = DruidConnectionManager.getConnectionInfo().getDatabaseType();

        Class<? extends TableStructure> classType = TableStructureFactory.getTableStructureType(dbType);

        List<Field> fields = ReflectionUtils.getFieldAllContainSuperclass(classType, field -> true);

        List<LabelAndValue> labelAndValues = fields.stream()
                .sorted(Comparator.comparingInt(field -> field.getAnnotation(TableField.class).order()))
                .map(field -> new LabelAndValue(field.getAnnotation(TableField.class).value(), field.getName()))
                .collect(Collectors.toList());

        List<String> defaultColumns = fields.stream()
                .filter(field -> !field.getAnnotation(TableField.class).exclude())
                .sorted(Comparator.comparingInt(field -> field.getAnnotation(TableField.class).order()))
                .map(Field::getName)
                .collect(Collectors.toList());

        columnsVo.setAllColumns(labelAndValues);

        columnsVo.setDefaultColumns(defaultColumns);

        return ResponseResult.success(columnsVo);
    }


    @SneakyThrows(Exception.class)
    public byte[] buildWordDocument(TargetTableDto targetTableDto) {

        DefaultColumnManager.setDefaultColumns(targetTableDto.getDefaultColumns());

        WordBuilder wordBuilder = WordBuilder.create();

        List<TreeNode> treeNodeList = targetTableDto.getDataList();

        if (!CollectionUtils.isEmpty(treeNodeList)) {

            //过滤出全部 Schema节点
            List<TreeNode> schemaNodes = targetTableDto.getDataList().stream().filter(TreeNode::isSchemaNode).collect(Collectors.toList());

            WordStyle.Font calibri = WordStyle.Font.fontFamily("Calibri");
            WordStyle.Font bold = WordStyle.Font.BOLD;
            WordStyle.Font titleFontSize = WordStyle.Font.fontSize(20);
            WordStyle.Font tableNameFontSize = WordStyle.Font.fontSize(15);
            WordStyle.Font cellFontSize = WordStyle.Font.fontSize(10);

            for (TreeNode schemaNode : schemaNodes) {

                String schemaName = schemaNode.getLabelName();

                wordBuilder.addParagraph("数据库名称 " + schemaName, calibri, bold, titleFontSize);

                //过滤出指定 Schema节点下的全部 table节点
                List<TreeNode> tableNodes = treeNodeList.stream()
                        .filter(data -> !data.isSchemaNode() && Objects.equals(data.getParentNodeId(), schemaNode.getNodeId()))
                        .collect(Collectors.toList());

                List<Future<TableInfo>> futureList = new ArrayList<>();

                for (TreeNode tableNode : tableNodes) {
                    Future<TableInfo> future = threadPoolExecutor.submit(() -> {
                                String tableName = tableNode.getLabelName();
                                String tableComment = tableNode.getDescription();
                                TableInfo tableInfo = new TableInfo();
                                TableDefinition tableDefinition = new TableDefinition(tableName, tableComment);
                                tableDefinition.setTableName(tableName);
                                tableDefinition.setTableComment(tableComment);
                                List<? extends TableStructure> tableStructures = tableMapper.getTableStructureList(schemaName, tableName);
                                tableInfo.setTableDefinition(tableDefinition);
                                tableInfo.setDataList(tableStructures);
                                return tableInfo;
                            }
                    );
                    futureList.add(future);
                }

                for (Future<TableInfo> future : futureList) {
                    TableInfo tableInfo = future.get();
                    TableDefinition tableDefinition = tableInfo.getTableDefinition();
                    wordBuilder.addParagraph(!StringUtils.hasText(tableDefinition.getTableComment())
                            ? tableDefinition.getTableName()
                            : tableDefinition.getTableName() + "  " + tableDefinition.getTableComment(), calibri, tableNameFontSize);
                    wordBuilder.addTable(tableInfo.getDataList(), calibri, cellFontSize);
                    wordBuilder.addCarriageReturn().addCarriageReturn();
                }
            }
        }
        return wordBuilder.asBytes();
    }


    @SneakyThrows(Exception.class)
    public byte[] buildPdfDocument(TargetTableDto targetTableDto) {

        DefaultColumnManager.setDefaultColumns(targetTableDto.getDefaultColumns());

        PDFBuilder pdfBuilder = PDFBuilder.create(PageSize.A4, 90, 90, 36, 36);

        List<TreeNode> treeNodeList = targetTableDto.getDataList();

        if (!CollectionUtils.isEmpty(treeNodeList)) {

            //过滤出全部 Schema节点
            List<TreeNode> schemaNodes = targetTableDto.getDataList().stream().filter(TreeNode::isSchemaNode).collect(Collectors.toList());

            PdfStyle.Font bold = PdfStyle.Font.BOLD;

            PdfStyle.Font titleFontSize = PdfStyle.Font.fontSize(20);

            PdfStyle.Font tableNameFontSize = PdfStyle.Font.fontSize(15);

            PdfStyle.Font cellFontSize = PdfStyle.Font.fontSize(10);

            PdfStyle.Paragraph paragraphSpacing = PdfStyle.Paragraph.paragraphSpacing(1.3f);


            for (TreeNode schemaNode : schemaNodes) {

                String schemaName = schemaNode.getLabelName();

                pdfBuilder.addParagraph("数据库名称 " + schemaName, bold, titleFontSize);

                //过滤出指定 Schema节点下的全部 table节点
                List<TreeNode> tableNodes = treeNodeList.stream()
                        .filter(data -> !data.isSchemaNode() && Objects.equals(data.getParentNodeId(), schemaNode.getNodeId()))
                        .collect(Collectors.toList());

                List<Future<TableInfo>> futureList = new ArrayList<>();

                for (TreeNode tableNode : tableNodes) {
                    Future<TableInfo> future = threadPoolExecutor.submit(() -> {
                                String tableName = tableNode.getLabelName();
                                String tableComment = tableNode.getDescription();
                                TableInfo tableInfo = new TableInfo();
                                TableDefinition tableDefinition = new TableDefinition(tableName, tableComment);
                                List<? extends TableStructure> tableStructures = tableMapper.getTableStructureList(schemaName, tableName);
                                tableInfo.setTableDefinition(tableDefinition);
                                tableInfo.setDataList(tableStructures);
                                return tableInfo;
                            }
                    );
                    futureList.add(future);
                }

                for (Future<TableInfo> future : futureList) {
                    TableInfo tableInfo = future.get();
                    TableDefinition tableDefinition = tableInfo.getTableDefinition();

                    pdfBuilder.addParagraph(!StringUtils.hasText(tableDefinition.getTableComment())
                            ? tableDefinition.getTableName()
                            : tableDefinition.getTableName() + "  " + tableDefinition.getTableComment(), tableNameFontSize, paragraphSpacing);
                    pdfBuilder.addTable(tableInfo.getDataList(), cellFontSize);
                    pdfBuilder.addCarriageReturn();
                    pdfBuilder.addCarriageReturn();
                }
            }
        }
        return pdfBuilder.asBytes();
    }


    @SneakyThrows(Exception.class)
    public byte[] buildMdDocument(TargetTableDto targetTableDto) {

        DefaultColumnManager.setDefaultColumns(targetTableDto.getDefaultColumns());

        MarkDownBuilder markDownBuilder = MarkDownBuilder.create();

        List<TreeNode> treeNodeList = targetTableDto.getDataList();

        if (!CollectionUtils.isEmpty(treeNodeList)) {

            //过滤出全部 Schema节点
            List<TreeNode> schemaNodes = targetTableDto.getDataList().stream().filter(TreeNode::isSchemaNode).collect(Collectors.toList());

            for (TreeNode schemaNode : schemaNodes) {

                String schemaName = schemaNode.getLabelName();

                markDownBuilder.title("数据库名称 " + schemaName, MarkDownStyle.Title.THIRD);

                //过滤出指定 Schema节点下的全部 table节点
                List<TreeNode> tableNodes = treeNodeList.stream()
                        .filter(data -> !data.isSchemaNode() && Objects.equals(data.getParentNodeId(), schemaNode.getNodeId()))
                        .collect(Collectors.toList());

                List<Future<TableInfo>> futureList = new ArrayList<>();

                for (TreeNode tableNode : tableNodes) {
                    Future<TableInfo> future = threadPoolExecutor.submit(() -> {
                                String tableName = tableNode.getLabelName();
                                String tableComment = tableNode.getDescription();
                                TableInfo tableInfo = new TableInfo();
                                TableDefinition tableDefinition = new TableDefinition(tableName, tableComment);
                                List<? extends TableStructure> tableStructures = tableMapper.getTableStructureList(schemaName, tableName);
                                tableInfo.setTableDefinition(tableDefinition);
                                tableInfo.setDataList(tableStructures);
                                return tableInfo;
                            }
                    );
                    futureList.add(future);
                }

                for (Future<TableInfo> future : futureList) {
                    TableInfo tableInfo = future.get();
                    TableDefinition tableDefinition = tableInfo.getTableDefinition();
                    markDownBuilder.text(!StringUtils.hasText(tableDefinition.getTableComment())
                            ? tableDefinition.getTableName()
                            : tableDefinition.getTableName() + "  " + tableDefinition.getTableComment(), MarkDownStyle.Font.BOLD);
                    markDownBuilder.table(tableInfo.getDataList(), MarkDownStyle.CellAlignment.LEFT);
                    markDownBuilder.blankRow();
                    markDownBuilder.blankRow();
                }
                markDownBuilder.blankRow();
            }
        }
        return markDownBuilder.asBytes();
    }


    @SneakyThrows(Exception.class)
    public byte[] buildHtmlDocument(TargetTableDto targetTableDto) {

        DefaultColumnManager.setDefaultColumns(targetTableDto.getDefaultColumns());

        HTMLBuilder htmlBuilder = HTMLBuilder.create("2%", "2%", "20%", "20%");
        htmlBuilder.setInnerCssStyle("table{font-size:12px}");

        HTMLStyle.Font bold = HTMLStyle.Font.BOLD;
        HTMLStyle.Font schemaNameFontSize = HTMLStyle.Font.fontsize("25px");
        HTMLStyle.Font tableNameFontSize = HTMLStyle.Font.fontsize("20px");
        HTMLStyle.Common paddingBottom = HTMLStyle.Common.paddingBottom("10px");

        List<TreeNode> treeNodeList = targetTableDto.getDataList();

        if (!CollectionUtils.isEmpty(treeNodeList)) {

            //过滤出全部 Schema节点
            List<TreeNode> schemaNodes = targetTableDto.getDataList().stream().filter(TreeNode::isSchemaNode).collect(Collectors.toList());

            for (TreeNode schemaNode : schemaNodes) {

                String schemaName = schemaNode.getLabelName();

                htmlBuilder.addParagraph("数据库名称 " + schemaName, bold, schemaNameFontSize, paddingBottom);

                //过滤出指定 Schema节点下的全部 table节点
                List<TreeNode> tableNodes = treeNodeList.stream()
                        .filter(data -> !data.isSchemaNode() && Objects.equals(data.getParentNodeId(), schemaNode.getNodeId()))
                        .collect(Collectors.toList());

                List<Future<TableInfo>> futureList = new ArrayList<>();

                for (TreeNode tableNode : tableNodes) {
                    Future<TableInfo> future = threadPoolExecutor.submit(() -> {
                                String tableName = tableNode.getLabelName();
                                String tableComment = tableNode.getDescription();
                                TableInfo tableInfo = new TableInfo();
                                TableDefinition tableDefinition = new TableDefinition(tableName, tableComment);
                                List<? extends TableStructure> tableStructures = tableMapper.getTableStructureList(schemaName, tableName);
                                tableInfo.setTableDefinition(tableDefinition);
                                tableInfo.setDataList(tableStructures);
                                return tableInfo;
                            }
                    );
                    futureList.add(future);
                }

                for (Future<TableInfo> future : futureList) {
                    TableInfo tableInfo = future.get();
                    TableDefinition tableDefinition = tableInfo.getTableDefinition();
                    htmlBuilder.addParagraph(!StringUtils.hasText(tableDefinition.getTableComment())
                            ? tableDefinition.getTableName()
                            : tableDefinition.getTableName() + "  " + tableDefinition.getTableComment(), bold, tableNameFontSize, paddingBottom);
                    htmlBuilder.addTable(tableInfo.getDataList());
                    htmlBuilder.blankRow();
                    htmlBuilder.blankRow();
                }
                htmlBuilder.blankRow();
                htmlBuilder.blankRow();
            }
        }
        return htmlBuilder.asBytes();
    }

}