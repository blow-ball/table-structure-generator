package com.geqian.structure.controller;

import com.geqian.structure.common.dto.ConnectionInfoDto;
import com.geqian.structure.common.ResponseResult;
import com.geqian.structure.service.ConnectionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author geqian
 * @date 21:50 2023/7/8
 */

@Controller
public class ConnectionController {

    @Resource
    private ConnectionService connectionService;

    @GetMapping("/")
    public String index() {
        return "index";
    }


    @GetMapping("/generator")
    public String generator() {
        return "generator";
    }

    @ResponseBody
    @PostMapping(value = "/dataSource/connection")
    public ResponseResult<String> connection(@RequestBody ConnectionInfoDto dataSourceDto) {
        return connectionService.connection(dataSourceDto);
    }
}