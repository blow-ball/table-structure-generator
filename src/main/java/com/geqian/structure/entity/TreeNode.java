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

    private String schema;

    private String key;

    private String value;

    private List<TreeNode> children;
}
