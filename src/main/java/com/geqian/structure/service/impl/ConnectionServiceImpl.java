package com.geqian.structure.service.impl;

import com.geqian.structure.common.ResponseResult;
import com.geqian.structure.common.dto.DataSourceDto;
import com.geqian.structure.db.DruidConnectionManager;
import com.geqian.structure.service.ConnectionService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Objects;

/**
 * @author geqian
 * @date 12:52 2023/7/9
 */
@Service
public class ConnectionServiceImpl implements ConnectionService {


    @Override
    public ResponseResult<String> connection(DataSourceDto dataSourceDto) {
        try {
            if (!Objects.equals(dataSourceDto, DruidConnectionManager.getDataSource())) {
                DruidConnectionManager.setDataSource(dataSourceDto);
                DruidConnectionManager.clearDatasource();
            }
            DruidConnectionManager.getConnection();
        } catch (SQLException e) {
            DruidConnectionManager.clearDatasource();
            e.printStackTrace();
            return ResponseResult.fail("请检查数据库连接信息是否有误！");
        } catch (Exception e) {
            DruidConnectionManager.clearDatasource();
            e.printStackTrace();
            return ResponseResult.fail(e.getMessage());
        }
        return ResponseResult.success("连接成功");
    }
}
