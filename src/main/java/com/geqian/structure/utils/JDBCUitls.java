package com.geqian.structure.utils;

import cn.hutool.core.convert.Convert;
import com.geqian.structure.annotation.Column;
import com.geqian.structure.db.DruidConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author geqian
 * @date 18:06 2023/11/11
 */
public class JDBCUitls {

    private static final Logger log = LoggerFactory.getLogger(JDBCUitls.class);

    /**
     * 通用查询单条数据方法
     *
     * @param sql        sql语句
     * @param args       参数
     * @param resultType 返回值类型
     */
    public static <T> T selectOne(String sql, Class<T> resultType, Object... args) {
        List<T> results = selectList(sql, resultType, args);
        if (results.size() == 0) {
            return null;
        }
        if (results.size() > 1) {
            throw new RuntimeException("查询到多条匹配记录！");
        }
        return results.get(0);
    }

    /**
     * 通用查询多条数据方法
     *
     * @param sql        sql语句
     * @param args       参数列表
     * @param resultType 返回值类型
     */
    public static <T> List<T> selectList(String sql, Class<T> resultType, Object... args) {
        PreparedStatement ps = null;

        //占位符个数
        int placeholderCount = getCharacterCount(sql, '?');

        //检测占位符和参数个数是否匹配
        if (placeholderCount != args.length) {
            throw new IllegalArgumentException("占位符个数与参数个数不一致！");
        }

        //获取连接
        Connection connection = DruidConnectionManager.getConnection();

        try {
            //预编译sql
            ps = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                //填充占位符
                ps.setObject(i + 1, args[i]);
                //sql占位符替换为具体值
                int indexOf = sql.indexOf("?");
                sql = sql.substring(0, indexOf) + args[i] + sql.substring(indexOf + 1);
            }
            //执行并获取结果集
            ResultSet resultSet = ps.executeQuery();
            //获取结果集元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            //列名与属性映射关系
            Map<String, Field> columnFieldMapping = columnFieldMapping(resultType, metaData);
            //结果数据集
            List<T> results = new ArrayList<>();
            while (resultSet.next()) {
                T instance = resultType.getConstructor().newInstance();
                for (Map.Entry<String, Field> entry : columnFieldMapping.entrySet()) {
                    String columnName = entry.getKey();
                    Field field = entry.getValue();
                    Object value = resultSet.getObject(columnName);
                    try {
                        field.set(instance, value);
                    } catch (Exception e) {
                        field.set(instance, Convert.convert(field.getType(), value));
                    }
                }
                results.add(instance);
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException("查询数据发生错误,", e);
        } finally {
            closeResource(ps, connection);
            log.info("\n==> {}", sql);
        }
    }


    /**
     * 通用更新方法
     *
     * @param sql  sql语句
     * @param args 参数
     */
    public static int update(String sql, Object... args) {
        return executeUpdate(sql, args);
    }

    /**
     * 通用删除方法
     *
     * @param sql  sql语句
     * @param args 参数
     */
    public static int delete(String sql, Object... args) {
        return executeUpdate(sql, args);
    }


    /**
     * 通用插入方法
     *
     * @param sql  sql语句
     * @param args 参数
     */
    public static int insert(String sql, Object... args) {
        return executeUpdate(sql, args);
    }


    /**
     * 通用更新方法
     *
     * @param sql  sql语句
     * @param args 参数
     */
    private static int executeUpdate(String sql, Object... args) {
        PreparedStatement ps = null;
        //影响数据条数
        int affectedRows = 0;
        //占位符个数
        int placeholderCount = getCharacterCount(sql, '?');

        if (placeholderCount != args.length) {
            throw new IllegalArgumentException("占位符个数与参数个数不一致！");
        }
        //获取连接
        Connection connection = DruidConnectionManager.getConnection();
        try {
            //预编译
            ps = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                //填充占位符
                ps.setObject(i + 1, args[i]);
                sql = sql.replaceFirst("\\?", String.valueOf(args[i]));
            }
            affectedRows = ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //关闭资源
            closeResource(ps, connection);
            log.info("\n==> {}", sql);
        }
        return affectedRows;
    }


    /**
     * 列名与属性映射关系
     *
     * @param objectClass 实体类类型
     * @param metaData    表元信息
     * @return
     */
    private static Map<String, Field> columnFieldMapping(Class<?> objectClass, ResultSetMetaData metaData) {

        Map<String, Field> columnFieldMapping = new HashMap<>();

        Map<String, Field> fieldMap = getFieldMapContainSuperclass(objectClass, field -> true);

        Collection<Field> fields = fieldMap.values();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                String columnName = field.getAnnotation(Column.class).name();
                columnFieldMapping.put(columnName, field);
            }
        }
        try {
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnLabel(i + 1);
                if (!columnFieldMapping.containsKey(columnName)) {
                    try {
                        String fieldName = underlineToSmallHump(columnName);
                        Field field = fieldMap.get(fieldName);
                        if (field != null) {
                            columnFieldMapping.put(columnName, field);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
            return columnFieldMapping;
        } catch (Exception e) {
            throw new RuntimeException("Error in obtaining column name and field mapping!", e);
        }
    }


    /**
     * 下划线转小驼峰命名
     *
     * @param input
     * @return
     */
    private static String underlineToSmallHump(String input) {
        StringBuilder sb = new StringBuilder();
        String[] parts = input.split("_");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == 0) {
                sb.append(part);
            } else {
                sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
            }
        }
        return sb.toString();
    }


    /**
     * 关闭资源
     *
     * @param statement
     */
    private static void closeResource(Statement statement, Connection connection) {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static int getCharacterCount(String str, char target) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c == target) {
                count++;
            }
        }
        return count;
    }


    /**
     * 获取Class对象及父类Class对象全部属性，并堆属性进行条件过滤
     *
     * @param classType
     * @param condition
     * @return
     */
    private static Map<String, Field> getFieldMapContainSuperclass(Class<?> classType, Predicate<Field> condition) {

        List<Class<?>> classes = new ArrayList<>();

        //遍历获取父类class
        while (!Objects.equals(classType, Object.class)) {
            classes.add(classType);
            classType = classType.getSuperclass();
        }

        return classes.stream()
                .map(pojoClass -> Stream.of(pojoClass.getDeclaredFields()).filter(condition).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toMap(Field::getName, Function.identity(), (oldVal, newVal) -> oldVal));
    }

}
