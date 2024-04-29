package com.geqian.structure.service.impl;

import com.geqian.structure.common.ResponseResult;
import com.geqian.structure.common.dto.ConnectionInfoDto;
import com.geqian.structure.jdbc.DruidConnectionManager;
import com.geqian.structure.service.ConnectionService;
import org.apache.derby.iapi.error.StandardException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author geqian
 * @date 12:52 2023/7/9
 */
@Service
public class ConnectionServiceImpl implements ConnectionService {


    @Override
    public ResponseResult<String> connection(ConnectionInfoDto connectionInfoDto) {
        try {
            if (!Objects.equals(connectionInfoDto, DruidConnectionManager.getConnectionInfo())) {
                DruidConnectionManager.setConnectionInfo(connectionInfoDto);
                DruidConnectionManager.clearDatasource();
            }
            DruidConnectionManager.getConnection();
        } catch (Exception e) {
            DruidConnectionManager.clearDatasource();
            Throwable cause = e.getCause();
            if (cause != null) {
                Throwable throwable = cause.getCause();
                if (throwable instanceof StandardException) {
                    return ResponseResult.fail("数据库已被另一个实例占用");
                }
            }
            return ResponseResult.fail(e.getMessage());
        }
        return ResponseResult.success("连接成功");
    }
}
