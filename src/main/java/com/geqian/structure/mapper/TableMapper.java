package com.geqian.structure.mapper;

import com.geqian.structure.db.CurrentDatabaseManager;
import com.geqian.structure.db.DatabaseManager;
import com.geqian.structure.jdbc.DruidConnectionManager;
import com.geqian.structure.jdbc.JDBCHelper;
import com.geqian.structure.entity.TableStructure;
import com.geqian.structure.entity.TableStructureFactory;
import com.geqian.structure.entity.TableDefinition;
import com.geqian.structure.entity.TreeNode;
import com.geqian.structure.utils.UUIDUtils;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author geqian
 * @date 15:56 2023/11/11
 */
@Component
public class TableMapper {


    @Resource
    private JDBCHelper jdbcHelper;

    /**
     * 获取所有Databases
     *
     * @return
     * @throws Exception
     */
    public List<TreeNode> getDatabases() {
        DatabaseManager databaseManager = CurrentDatabaseManager.getDatabaseManager();
        String sql = databaseManager.getDatabases();
        List<TreeNode> treeNodeList = jdbcHelper.selectList(sql, TreeNode.class);
        for (TreeNode treeNode : treeNodeList) {
            treeNode.setSchemaNode(true);
            treeNode.setNodeId(UUIDUtils.generateUUID());
            treeNode.setChildrenCount(getTableCount(treeNode.getLabelName()));
        }
        return treeNodeList;
    }


    /**
     * 获取指定 database下所有表名
     *
     * @param schemaName
     * @return
     * @throws Exception
     */
    @SneakyThrows(Exception.class)
    public List<TreeNode> getTables(String schemaName, String parentNodeId) {
        DatabaseManager databaseManager = CurrentDatabaseManager.getDatabaseManager();
        String sql = databaseManager.getTables();
        List<TreeNode> tableNodeList = jdbcHelper.selectList(sql, TreeNode.class, schemaName);
        for (TreeNode treeNode : tableNodeList) {
            treeNode.setSchemaNode(false);
            treeNode.setNodeId(UUIDUtils.generateUUID());
            treeNode.setParentNodeId(parentNodeId);
        }
        return tableNodeList;
    }

    /**
     * 获取指定 database下所有表名
     *
     * @param schemaName
     * @return
     * @throws Exception
     */
    @SneakyThrows(Exception.class)
    public Integer getTableCount(String schemaName) {
        DatabaseManager databaseManager = CurrentDatabaseManager.getDatabaseManager();
        String sql = databaseManager.getTables();
        List<TreeNode> tableNodeList = jdbcHelper.selectList(sql, TreeNode.class, schemaName);
        return tableNodeList.size();
    }


    /**
     * 获取指定schema下指定表信息
     *
     * @param schemaName
     * @return
     * @throws Exception
     */
    @SneakyThrows(Exception.class)
    public TableDefinition getTableInfo(String schemaName, String tableName) {
        DatabaseManager databaseManager = CurrentDatabaseManager.getDatabaseManager();
        String sql = databaseManager.getTableInfo();
        return jdbcHelper.selectOne(sql, TableDefinition.class, schemaName, tableName);
    }


    /**
     * 获取指定schema下指定表的详细信息
     *
     * @param schemaName
     * @param tableName
     * @return
     * @throws Exception
     */
    @SneakyThrows(Exception.class)
    public List<? extends TableStructure> getTableStructureList(String schemaName, String tableName) {
        String databaseType = DruidConnectionManager.getConnectionInfo().getDatabaseType();
        Class<? extends TableStructure> classType = TableStructureFactory.getTableStructureType(databaseType);
        if (classType != null) {
            DatabaseManager databaseManager = CurrentDatabaseManager.getDatabaseManager();
            String sql = databaseManager.getTableStructure();
            List<? extends TableStructure> tableStructures = jdbcHelper.selectList(sql, classType, schemaName, tableName);
            for (int i = 0; i < tableStructures.size(); i++) {
                tableStructures.get(i).setNumber(i + 1);
            }
            return tableStructures;
        }
        return Collections.emptyList();
    }

}
