package com.geqian.structure.service.impl;

import cn.hutool.core.io.IoUtil;
import com.geqian.structure.common.ResponseResult;
import com.geqian.structure.common.dto.TargetTableDto;
import com.geqian.structure.common.vo.ColumnsVo;
import com.geqian.structure.db.DefaultColumnManager;
import com.geqian.structure.db.DruidConnectionManager;
import com.geqian.structure.entity.TableDefinition;
import com.geqian.structure.entity.TableStructure;
import com.geqian.structure.entity.TableStructureFactory;
import com.geqian.structure.entity.TreeNode;
import com.geqian.structure.mapper.TableMapper;
import com.geqian.structure.pdf.PDFBuilder;
import com.geqian.structure.pojo.LabelAndValue;
import com.geqian.structure.pojo.TableInfo;
import com.geqian.structure.service.GeneratorService;
import com.geqian.structure.utils.ReflectionUtils;
import com.geqian.structure.word.ParagraphConfig;
import com.geqian.structure.word.WordBytesBuilder;
import com.geqian.structure.word.WordTableField;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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


    @Override
    public ResponseResult<List<TreeNode>> selectTableStructure() {
        return ResponseResult.success(tableMapper.getTableTree());
    }

    @SneakyThrows(Exception.class)
    @Override
    public void preview(TargetTableDto targetTableDto, HttpServletResponse response) {

        byte[] pdfBytes = buildPdfDocument(targetTableDto);

        //byte[] pdfBytes = WordToPdfUtils.word2007ToPdf(wordBytes);
        response.setHeader("content-type", "application/octet-stream");
        response.setHeader("filename", URLEncoder.encode("数据库表结构" + ".pdf", "UTF-8"));
        //文件设置为附件
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("数据库表结构" + ".pdf", "UTF-8"));
        IoUtil.write(response.getOutputStream(), true, pdfBytes);
    }

    @SneakyThrows(Exception.class)
    @Override
    public void download(TargetTableDto targetTableDto, HttpServletResponse response) {

        byte[] wordBytes = buildWordDocument(targetTableDto);

        response.setHeader("content-type", "application/octet-stream");
        response.setHeader("filename", URLEncoder.encode("数据库表结构" + ".docx", "UTF-8"));
        //文件设置为附件
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("数据库表结构" + ".docx", "UTF-8"));
        IoUtil.write(response.getOutputStream(), true, wordBytes);
    }


    @Override
    public ResponseResult<ColumnsVo> getTableColumnInfo() {

        ColumnsVo columnsVo = new ColumnsVo();

        String dbType = DruidConnectionManager.getConnectionInfo().getDatabaseType();

        Class<? extends TableStructure> classType = TableStructureFactory.getTableStructureType(dbType);

        List<Field> fields = ReflectionUtils.getFieldAllContainSuperclass(classType, field -> true);

        List<LabelAndValue> labelAndValues = fields.stream()
                .sorted(Comparator.comparingInt(field -> field.getAnnotation(WordTableField.class).order()))
                .map(field -> new LabelAndValue(field.getAnnotation(WordTableField.class).value(), field.getName()))
                .collect(Collectors.toList());

        List<String> defaultColumns = fields.stream()
                .filter(field -> !field.getAnnotation(WordTableField.class).exclude())
                .sorted(Comparator.comparingInt(field -> field.getAnnotation(WordTableField.class).order()))
                .map(Field::getName)
                .collect(Collectors.toList());

        columnsVo.setAllColumns(labelAndValues);

        columnsVo.setDefaultColumns(defaultColumns);

        return ResponseResult.success(columnsVo);
    }


    @SneakyThrows(Exception.class)
    public byte[] buildWordDocument(TargetTableDto targetTableDto) {

        DefaultColumnManager.setDefaultColumns(targetTableDto.getDefaultColumns());

        WordBytesBuilder wordBytesBuilder = WordBytesBuilder.create();

        List<TreeNode> treeNodeList = targetTableDto.getDataList();

        if (!CollectionUtils.isEmpty(treeNodeList)) {

            //过滤出全部 Schema节点
            List<TreeNode> schemaNodes = targetTableDto.getDataList().stream().filter(data -> Objects.isNull(data.getTableName())).collect(Collectors.toList());

            ParagraphConfig tableDescConfig = ParagraphConfig.create();

            tableDescConfig.getFontConfig()
                    .setFontFamily("Calibri")
                    .setFontSize(20);

            ParagraphConfig tableCellConfig = ParagraphConfig.create();

            tableCellConfig.getFontConfig().setFontFamily("Calibri");

            for (TreeNode schemaNode : schemaNodes) {
                String schemaName = schemaNode.getSchemaName();
                wordBytesBuilder.addParagraph("数据库 " + schemaName, tableDescConfig);

                //过滤出指定 Schema节点下的全部 table节点
                List<TreeNode> tableNodes = treeNodeList.stream()
                        .filter(data -> Objects.equals(data.getSchemaName(), schemaName) && !Objects.equals(data.getTableName(), null))
                        .collect(Collectors.toList());

                List<Future<TableInfo>> futureList = new ArrayList<>();

                for (TreeNode tableNode : tableNodes) {
                    Future<TableInfo> future = threadPoolExecutor.submit(() -> {
                                TableInfo tableInfo = new TableInfo();
                                TableDefinition tableDefinition = tableMapper.getTableInfo(schemaName, tableNode.getTableName());
                                List<? extends TableStructure> tableStructures = tableMapper.getTableStructureList(schemaName, tableNode.getTableName());
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
                    tableDescConfig.getFontConfig().setFontSize(15);
                    wordBytesBuilder.addParagraph(!StringUtils.hasText(tableDefinition.getTableComment()) ? tableDefinition.getTableName() : tableDefinition.getTableComment() + "  " + tableDefinition.getTableName(), tableDescConfig);
                    tableCellConfig.getFontConfig().setFontSize(10);
                    wordBytesBuilder.addTable(tableInfo.getDataList(), tableCellConfig);
                    wordBytesBuilder.addCarriageReturn().addCarriageReturn();
                }
            }
        }
        return wordBytesBuilder.asBytes();
    }


    @SneakyThrows(Exception.class)
    public byte[] buildPdfDocument(TargetTableDto targetTableDto) {

        DefaultColumnManager.setDefaultColumns(targetTableDto.getDefaultColumns());

        PDFBuilder pdfBuilder = PDFBuilder.create();

        List<TreeNode> treeNodeList = targetTableDto.getDataList();

        if (!CollectionUtils.isEmpty(treeNodeList)) {

            //过滤出全部 Schema节点
            List<TreeNode> schemaNodes = targetTableDto.getDataList().stream().filter(data -> Objects.isNull(data.getTableName())).collect(Collectors.toList());

            com.geqian.structure.pdf.ParagraphConfig tableDescConfig = com.geqian.structure.pdf.ParagraphConfig.create();

            tableDescConfig.getFontConfig().setFontSize(16);

            tableDescConfig.setFirstLineIndent(3f);

            com.geqian.structure.pdf.ParagraphConfig tableCellConfig = com.geqian.structure.pdf.ParagraphConfig.create();

            for (TreeNode schemaNode : schemaNodes) {
                String schemaName = schemaNode.getSchemaName();

                pdfBuilder.addParagraph("数据库 " + schemaName, tableDescConfig);

                //过滤出指定 Schema节点下的全部 table节点
                List<TreeNode> tableNodes = treeNodeList.stream()
                        .filter(data -> Objects.equals(data.getSchemaName(), schemaName) && !Objects.equals(data.getTableName(), null))
                        .collect(Collectors.toList());

                List<Future<TableInfo>> futureList = new ArrayList<>();

                for (TreeNode tableNode : tableNodes) {
                    Future<TableInfo> future = threadPoolExecutor.submit(() -> {
                                TableInfo tableInfo = new TableInfo();
                                TableDefinition tableDefinition = tableMapper.getTableInfo(schemaName, tableNode.getTableName());
                                List<? extends TableStructure> tableStructures = tableMapper.getTableStructureList(schemaName, tableNode.getTableName());
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
                    pdfBuilder.addParagraph(!StringUtils.hasText(tableDefinition.getTableComment()) ? tableDefinition.getTableName() : tableDefinition.getTableComment() + "  " + tableDefinition.getTableName(), tableDescConfig);
                    pdfBuilder.addTable(tableInfo.getDataList(), tableCellConfig);
                    pdfBuilder.addCarriageReturn();
                    pdfBuilder.addCarriageReturn();
                }
            }
        }
        return pdfBuilder.asBytes();
    }

}