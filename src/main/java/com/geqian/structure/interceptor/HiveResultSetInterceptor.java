package com.geqian.structure.interceptor;

import com.geqian.structure.entity.TreeNode;
import com.geqian.structure.jdbc.ResultSetInterceptor;

import java.sql.ResultSet;
import java.util.List;

/**
 * @author geqian
 * @date 22:40 2024/4/19
 */
//@Component
public class HiveResultSetInterceptor implements ResultSetInterceptor<TreeNode> {


    @Override
    public boolean support(String productName, String sql) {
        return false;
    }


    @Override
    public List<TreeNode> intercept(ResultSet resultSet) {
        return null;
    }

}
