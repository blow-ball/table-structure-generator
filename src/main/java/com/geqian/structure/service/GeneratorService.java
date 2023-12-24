package com.geqian.structure.service;

import com.geqian.structure.common.ResponseResult;
import com.geqian.structure.common.dto.TargetTableDto;
import com.geqian.structure.common.vo.ColumnsVo;
import com.geqian.structure.entity.TreeNode;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author geqian
 * @date 12:03 2023/7/12
 */
public interface GeneratorService {

    ResponseResult<List<TreeNode>> selectTableStructure();

    void preview(TargetTableDto targetTableDto, HttpServletResponse response);

    void download(TargetTableDto targetTableDto, HttpServletResponse response);

    ResponseResult<ColumnsVo> getTableColumnInfo();

}
