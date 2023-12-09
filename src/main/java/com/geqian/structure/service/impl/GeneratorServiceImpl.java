package com.geqian.structure.service.impl;

import cn.hutool.core.io.IoUtil;
import com.geqian.structure.common.ResponseResult;
import com.geqian.structure.common.dto.TargetTableDto;
import com.geqian.structure.common.vo.ColumnsVo;
import com.geqian.structure.db.DefaultColumnManager;
import com.geqian.structure.db.DruidConnectionManager;
import com.geqian.structure.entity.AbstractColumnContainer;
import com.geqian.structure.entity.ColumnContainerFactory;
import com.geqian.structure.entity.TableDefinition;
import com.geqian.structure.entity.TreeNode;
import com.geqian.structure.mapper.TableMapper;
import com.geqian.structure.pojo.TableInfo;
import com.geqian.structure.pojo.LabelAndValue;
import com.geqian.structure.service.GeneratorService;
import com.geqian.structure.utils.WordToPdfUtils;
import com.geqian.structure.word.ParagraphTextConfig;
import com.geqian.structure.word.ParagraphTextConfigBuilder;
import com.geqian.structure.word.TableField;
import com.geqian.structure.word.WordBytesBuilder;
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


    @Override
    public ResponseResult<List<TreeNode>> selectTableStructure() {
        return ResponseResult.success(tableMapper.getTableTree());
    }

    @SneakyThrows(Exception.class)
    @Override
    public void preview(TargetTableDto targetTableDto, HttpServletResponse response) {
        byte[] wordBytes = generateWord(targetTableDto);
        byte[] pdfBytes = WordToPdfUtils.word2007ToPdf(wordBytes);
        response.setHeader("content-type", "application/octet-stream");
        response.setHeader("filename", URLEncoder.encode("数据库表结构" + ".pdf", "UTF-8"));
        //文件设置为附件
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("数据库表结构" + ".pdf", "UTF-8"));
        IoUtil.write(response.getOutputStream(), true, pdfBytes);
    }

    @SneakyThrows(Exception.class)
    @Override
    public void download(TargetTableDto targetTableDto, HttpServletResponse response) {
        byte[] wordBytes = generateWord(targetTableDto);
        response.setHeader("content-type", "application/octet-stream");
        response.setHeader("filename", URLEncoder.encode("数据库表结构" + ".docx", "UTF-8"));
        //文件设置为附件
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("数据库表结构" + ".docx", "UTF-8"));
        IoUtil.write(response.getOutputStream(), true, wordBytes);
    }

    @Override
    public ResponseResult<ColumnsVo> getTableColumns() {
        ColumnsVo columnsVo = new ColumnsVo();
        String dbType = DruidConnectionManager.getDataSource().getDatabaseType();
        Collection<Field> fields = ColumnContainerFactory.getColumnContainer(dbType).getColumnFieldMapping().values();
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
    public byte[] generateWord(TargetTableDto targetTableDto) {

        DefaultColumnManager.setDefaultColumns(targetTableDto.getDefaultColumns());

        WordBytesBuilder wordBytesBuilder = WordBytesBuilder.create();

        List<TreeNode> dataList = targetTableDto.getDataList();

        if (!CollectionUtils.isEmpty(dataList)) {
            List<TreeNode> treeNodes = targetTableDto.getDataList().stream().filter(data -> Objects.isNull(data.getSchema())).collect(Collectors.toList());

            ParagraphTextConfig tableDescConfig = ParagraphTextConfigBuilder.create()
                    .setFontFamily("Calibri")
                    .setFontSize(20);

            ParagraphTextConfig tableCellConfig = ParagraphTextConfigBuilder.create()
                    .setFontFamily("Calibri");

            for (TreeNode treeNode : treeNodes) {
                String schemaName = treeNode.getValue();
                wordBytesBuilder.addParagraphText("数据库 " + schemaName, tableDescConfig);
                List<TreeNode> databaseDataList = dataList.stream().filter(data -> Objects.equals(data.getSchema(), schemaName)).collect(Collectors.toList());

                List<Future<TableInfo>> tableList = new ArrayList<>();
                for (TreeNode databaseData : databaseDataList) {
                    Future<TableInfo> future = threadPoolExecutor.submit(() -> {
                                TableInfo tableInfo = new TableInfo();
                                TableDefinition tableDefinition = tableMapper.getTableInfo(schemaName, databaseData.getValue());
                                List<AbstractColumnContainer> tableColumnInfoList = tableMapper.getTableColumnInfoList(schemaName, databaseData.getValue());
                                tableInfo.setTableDefinition(tableDefinition);
                                tableInfo.setDataList(tableColumnInfoList);
                                return tableInfo;
                            }
                    );
                    tableList.add(future);
                }

                for (Future<TableInfo> future : tableList) {
                    TableInfo tableInfo = future.get();
                    TableDefinition tableDefinition = tableInfo.getTableDefinition();
                    wordBytesBuilder.addParagraphText(!StringUtils.hasText(tableDefinition.getTableComment()) ? tableDefinition.getTableName() : tableDefinition.getTableComment() + "  " + tableDefinition.getTableName(), tableDescConfig.setFontSize(15));
                    wordBytesBuilder.addTable(tableInfo.getDataList(), tableCellConfig.setFontSize(10));
                    wordBytesBuilder.addCarriageReturn().addCarriageReturn();
                }
            }
        }
        return wordBytesBuilder.build();
    }
}