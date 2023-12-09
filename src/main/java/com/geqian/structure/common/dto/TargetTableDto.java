package com.geqian.structure.common.dto;

import com.geqian.structure.entity.TreeNode;
import lombok.Data;

import java.util.List;

/**
 * 生成表结构相关信息
 * @author geqian
 * @date 23:10 2023/7/10
 */
@Data
public class TargetTableDto {

    private List<String> defaultColumns;

    private List<TreeNode> dataList;
}
