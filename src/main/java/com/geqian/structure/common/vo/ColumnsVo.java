package com.geqian.structure.common.vo;

import com.geqian.structure.pojo.LabelAndValue;
import lombok.Data;

import java.util.List;

/**
 * @author geqian
 * @date 21:53 2023/9/20
 */
@Data
public class ColumnsVo {

    private List<LabelAndValue> allColumns;

    private List<String> defaultColumns;
}
