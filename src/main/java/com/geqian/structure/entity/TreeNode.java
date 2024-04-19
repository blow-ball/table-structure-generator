package com.geqian.structure.entity;

import lombok.Data;

import java.util.List;

/**
 * @author geqian
 * @date 16:18 2023/11/11
 */
@Data
public class TreeNode {

    private boolean schemaNode;

    private String nodeId;

    private String parentNodeId;

    private String labelName;

    private String description;

    private List<TreeNode> children;

    private Integer childrenCount;
}
