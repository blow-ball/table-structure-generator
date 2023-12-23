package com.geqian.structure.mapper;

import com.geqian.structure.db.CurrentDatabaseManager;
import com.geqian.structure.db.DatabaseManager;
import com.geqian.structure.db.DruidConnectionManager;
import com.geqian.structure.entity.AbstractColumnContainer;
import com.geqian.structure.entity.ColumnContainerFactory;
import com.geqian.structure.entity.TableDefinition;
import com.geqian.structure.entity.TreeNode;
import com.geqian.structure.utils.UUIDUtils;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author geqian
 * @date 15:56 2023/11/11
 */
@Component
public class TableMapper {

    /**
     * 获取所有schema
     *
     * @return
     * @throws Exception
     */
    @SneakyThrows(Exception.class)
    public List<TreeNode> getTableTree() {
        ResultSet resultSet = null;
        List<TreeNode> treeNodes = new ArrayList<>();
        try (Connection connection = DruidConnectionManager.getConnection()) {
            DatabaseManager databaseManager = CurrentDatabaseManager.getDatabaseManager();
            String sql = databaseManager.getDatabases();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String key = UUIDUtils.generateUUID();
                String schemaName = resultSet.getString("schemaName");
                TreeNode treeNode = new TreeNode();
                treeNode.setKey(key);
                treeNode.setValue(schemaName);
                List<TreeNode> tables = getTables(schemaName, key);
                treeNode.setChildren(tables);
                treeNodes.add(treeNode);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (Objects.nonNull(resultSet)) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return treeNodes;
    }


    /**
     * 获取指定schema下所有表名
     *
     * @param schemaName
     * @return
     * @throws Exception
     */
    @SneakyThrows(Exception.class)
    public List<TreeNode> getTables(String schemaName, String parentKey) {
        ResultSet resultSet = null;
        List<TreeNode> tableList = new ArrayList<>();
        try (Connection connection = DruidConnectionManager.getConnection()) {
            DatabaseManager databaseManager = CurrentDatabaseManager.getDatabaseManager();
            String sql = databaseManager.getTables();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, schemaName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TreeNode treeNode = new TreeNode();
                treeNode.setKey(UUIDUtils.generateUUID());
                treeNode.setValue(resultSet.getString("tableName"));
                treeNode.setSchema(schemaName);
                treeNode.setParentKey(parentKey);
                tableList.add(treeNode);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (Objects.nonNull(resultSet)) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return tableList;
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
        ResultSet resultSet = null;
        TableDefinition tableDefinition = new TableDefinition();
        try (Connection connection = DruidConnectionManager.getConnection()) {
            DatabaseManager databaseManager = CurrentDatabaseManager.getDatabaseManager();
            String sql = databaseManager.getTableInfo();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, schemaName);
            preparedStatement.setString(2, tableName);
            resultSet = preparedStatement.executeQuery();
            tableDefinition.setTableName(tableName);
            tableDefinition.setTableSchema(schemaName);
            while (resultSet.next()) {
                String tableComment = resultSet.getString("tableComment");
                tableDefinition.setTableComment(tableComment);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (Objects.nonNull(resultSet)) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return tableDefinition;
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
    public List<AbstractColumnContainer> getTableColumnInfoList(String schemaName, String tableName) {
        String databaseType = DruidConnectionManager.getDataSource().getDatabaseType();
        List<AbstractColumnContainer> columnContainerList = new ArrayList<>();
        ResultSet resultSet = null;
        try (Connection connection = DruidConnectionManager.getConnection()) {
            DatabaseManager databaseManager = CurrentDatabaseManager.getDatabaseManager();
            String sql = databaseManager.getTableStructure();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, schemaName);
            preparedStatement.setString(2, tableName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                AbstractColumnContainer columnContainer = ColumnContainerFactory.getColumnContainer(databaseType);
                List<Field> fields = columnContainer.getFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    String value = resultSet.getString(field.getName());
                    field.set(columnContainer,value);
                }
                columnContainerList.add(columnContainer);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (Objects.nonNull(resultSet)) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return columnContainerList;
    }

}
