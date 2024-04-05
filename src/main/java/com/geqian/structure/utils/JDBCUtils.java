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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * jdbc工具
 *
 * @author geqian
 * @date 18:06 2023/11/11
 */
public class JDBCUtils {

    private static final Logger log = LoggerFactory.getLogger(JDBCUtils.class);

    /**
     * 通用查询单条数据方法
     *
     * @param sql        sql语句
     * @param args       参数
     * @param resultType 返回值类型
     */
    public static <T> T selectOne(String sql, Class<T> resultType, Object... args) {
        List<T> results = selectList(sql, resultType, args);
        if (results.isEmpty()) {
            return null;
        } else if (results.size() > 1) {
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

        // 替换字符串映射
        List<Map.Entry<String, String>> replaceSymbols = findExpressions(sql, "(\\$\\{(\\d+)})", 1, 2);

        // 占位符字符串映射
        List<Map.Entry<String, String>> placeholders = findExpressions(sql, "(\\#\\{(\\d+)})", 1, 2);

        // 处理替换字符串
        for (Map.Entry<String, String> entry : replaceSymbols) {
            String expression = entry.getKey();
            String value = args[Integer.parseInt(entry.getValue())].toString();
            sql = replaceFirst(sql, expression, value);
        }

        // 处理预编译字符串
        for (Map.Entry<String, String> entry : placeholders) {
            String expression = entry.getKey();
            sql = replaceFirst(sql, expression, "?");
        }

        // 判断是否存在表达式，但没有参数
        if (replaceSymbols.size() + placeholders.size() != 0 && args.length == 0) {
            throw new IllegalArgumentException("占位符个数与参数个数不一致！");
        }

        // 填充参数后的 sql
        String completeSQL = sql;

        //获取连接
        Connection connection = DruidConnectionManager.getConnection();
        try {
            //预编译sql
            ps = connection.prepareStatement(sql);

            // 设置 sql 预编译参数
            for (int i = 0; i < placeholders.size(); i++) {
                Map.Entry<String, String> entry = placeholders.get(i);
                int index = Integer.parseInt(entry.getValue());
                Object arg = args[index];
                ps.setObject(i + 1, arg);
                completeSQL = replaceFirst(completeSQL, "?", arg.toString());
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
                    Object value = null;
                    try {
                        value = resultSet.getObject(columnName);
                        field.set(instance, value);
                    } catch (IllegalArgumentException e) {
                        field.set(instance, Convert.convert(field.getType(), value));
                    } catch (Exception ignore) {
                        // resultSet 获取不到 columnName（columnName不存在），不进行任何操作
                    }
                }
                results.add(instance);
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException("查询数据发生错误,", e);
        } finally {
            closeResource(ps, connection);
            log.info("\n==> {}", completeSQL);
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

        // 替换字符串映射
        List<Map.Entry<String, String>> replaceSymbols = findExpressions(sql, "(\\$\\{(\\d+)})", 1, 2);

        // 占位符字符串映射
        List<Map.Entry<String, String>> placeholders = findExpressions(sql, "(\\#\\{(\\d+)})", 1, 2);


        // 处理替换字符串
        for (Map.Entry<String, String> entry : replaceSymbols) {
            String expression = entry.getKey();
            String value = args[Integer.parseInt(entry.getValue())].toString();
            sql = replaceFirst(sql, expression, value);
        }


        for (Map.Entry<String, String> entry : placeholders) {
            String expression = entry.getKey();
            sql = replaceFirst(sql, expression, "?");
        }

        // 判断是否存在表达式，但没有参数
        if (replaceSymbols.size() + placeholders.size() != 0 && args.length == 0) {
            throw new IllegalArgumentException("占位符个数与参数个数不一致！");
        }

        // 填充参数后的 sql
        String completeSQL = sql;

        //获取连接
        Connection connection = DruidConnectionManager.getConnection();
        try {
            //预编译
            ps = connection.prepareStatement(sql);

            // 设置 sql 预编译参数
            for (int i = 0; i < placeholders.size(); i++) {
                Map.Entry<String, String> entry = placeholders.get(i);
                int index = Integer.parseInt(entry.getKey());
                Object arg = args[index];
                ps.setObject(i + 1, arg);
                completeSQL = replaceFirst(completeSQL, "?", arg.toString());
            }

            // 获取影响记录条数
            affectedRows = ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //关闭资源
            closeResource(ps, connection);
            log.info("\n==> {}", completeSQL);
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

        // 保证遍历时的顺序添加数据顺序（标注 Column 的注解的列名获取的值为映射属性的最终结果）
        Map<String, Field> columnFieldMapping = new LinkedHashMap<>();

        Map<String, Field> fieldMap = getFieldMapContainSuperclass(objectClass, field -> true);

        Collection<Field> fields = fieldMap.values();
        // 根据数据表列名获取列与属性映射关系
        try {
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnLabel(i + 1);
                String fieldName = columnName != null && columnName.contains("_")
                        ? underlineToSmallHump(columnName)
                        : columnName;
                Field field = fieldMap.get(fieldName);
                if (field != null) {
                    columnFieldMapping.put(columnName, field);
                }
            }
            // 额外添加 Column 设置列名与属性映射关系
            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    String columnName = field.getAnnotation(Column.class).name();
                    columnFieldMapping.put(columnName, field);
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


    /**
     * 将正则匹配字符串解析为键值对集合
     *
     * @param str             字符串
     * @param regex           匹配正则表达式
     * @param keyGroupIndex   键分组下标
     * @param valueGroupIndex 值分组下标
     * @return
     */
    public static List<Map.Entry<String, String>> findExpressions(String str, String regex, int keyGroupIndex, int valueGroupIndex) {
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(str);
        List<Map.Entry<String, String>> expressions = new ArrayList<>();
        while (matcher.find()) {
            String key = matcher.group(keyGroupIndex);
            String value = matcher.group(valueGroupIndex);
            expressions.add(new AbstractMap.SimpleEntry<>(key, value));
        }
        return expressions;
    }

    /**
     * 替换第一个匹配字符串（非正则表达式）
     *
     * @param original    原始字符串
     * @param search      被替换字符串
     * @param replacement 替换字符串
     * @return
     */
    private static String replaceFirst(String original, String search, String replacement) {
        // 寻找第一个匹配的字符串的位置
        int index = original.indexOf(search);

        // 如果找不到匹配的字符串，则返回原始字符串
        if (index == -1) {
            return original;
        }
        // 进行替换
        return original.substring(0, index) + replacement + original.substring(index + search.length());
    }
}
