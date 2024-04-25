package com.geqian.structure.interceptor;

import com.geqian.structure.entity.TreeNode;
import com.geqian.structure.jdbc.ResultSetInterceptor;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author geqian
 * @date 22:40 2024/4/19
 */
//@Component
public class HiveResultSetInterceptor implements ResultSetInterceptor<TreeNode> {


    @Override
    public boolean support(String productName, String sql) {
        return "hive".equalsIgnoreCase(productName) && formatSql("show databases").equals(sql);
    }


    @Override
    public List<TreeNode> intercept(ResultSet resultSet) throws Exception {
        List<TreeNode> treeNodes = new ArrayList<>();
        while (resultSet.next()) {
            TreeNode treeNode = new TreeNode();
            treeNode.setLabelName(resultSet.getString("Database"));
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

}
