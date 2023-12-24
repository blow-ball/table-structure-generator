package com.geqian.structure.entity;

import lombok.Data;

import java.util.List;

/**
 * @author geqian
 * @date 16:18 2023/11/11
 */
@Data
public class TreeNode {

    private String parentKey;

    private String key;

    private String label;

    private String schemaName;

    private String tableName;

    private List<TreeNode> children;
}
