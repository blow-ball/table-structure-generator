package com.geqian.structure.bo;

import com.geqian.structure.entity.TableStructure;
import com.geqian.structure.entity.TableDefinition;
import lombok.Data;

import java.util.List;

/**
 * 数据库基本表信息及字段信息
 * @author geqian
 * @date 12:19 2023/7/14
 */
@Data
public class TableInfo {

    private TableDefinition tableDefinition;

    private List<? extends TableStructure> dataList;
}
