package com.geqian.structure.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author geqian
 * @date 18:37 2024/4/15
 */
@Data
public class TableSelectDto {

    @JsonProperty("schemaName")
    private String schemaName;

    @JsonProperty("parentNodeId")
    private String parentNodeId;

}

