package com.geqian.structure.controller;

import com.geqian.structure.common.ResponseResult;
import com.geqian.structure.common.dto.TargetTableDto;
import com.geqian.structure.common.vo.ColumnsVo;
import com.geqian.structure.entity.TreeNode;
import com.geqian.structure.service.GeneratorService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author geqian
 * @date 12:01 2023/7/12
 */
@RestController
@RequestMapping("/generate")
public class GeneratorController {

    @Resource
    private GeneratorService generatorService;

    @ResponseBody
    @GetMapping("/getTableTree")
    public ResponseResult<List<TreeNode>> getTableTree() {
        return generatorService.selectTableStructure();
    }


    @GetMapping("/getTableColumns")
    public ResponseResult<ColumnsVo> getTableColumns() {
        return generatorService.getTableColumns();
    }


    @PostMapping("/pdf/preview")
    public void preview(@RequestBody TargetTableDto targetTableDto, HttpServletResponse response) throws Exception {
        generatorService.preview(targetTableDto, response);
    }


    @PostMapping("/download")
    public void download(@RequestBody TargetTableDto targetTableDto, HttpServletResponse response) throws Exception {
        generatorService.download(targetTableDto, response);
    }

}
