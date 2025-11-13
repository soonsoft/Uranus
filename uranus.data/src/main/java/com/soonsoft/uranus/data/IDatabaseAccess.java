package com.soonsoft.uranus.data;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.core.model.data.Page;

public interface IDatabaseAccess<TemplateType> {

    /**
     * 数据库Insert操作
     * @param commandText SQL语句或映射语句名称
     * @return 影响的行数
     */
    int insert(String commandText);

    /**
     * 数据库Insert操作
     * @param commandText SQL语句或映射语句名称
     * @param parameter 参数对象
     * @return 影响的行数
     */
    int insert(String commandText, Object parameter);

    /**
     * 数据库批量Insert操作
     * @param commandText SQL语句或映射语句名称
     * @param parameters 参数对象数组
     * @return 每个操作影响的行数数组
     */
    int[] insertBatch(String commandText, Object... parameters);

    /**
     * 数据库Update操作
     * @param commandText SQL语句或映射语句名称
     * @return 影响的行数
     */
    int update(String commandText);

    /**
     * 数据库Update操作
     * @param commandText SQL语句或映射语句名称
     * @param parameter 参数对象
     * @return 影响的行数
     */
    int update(String commandText, Object parameter);

    /**
     * 数据库批量Update操作
     * @param commandText SQL语句或映射语句名称
     * @param parameters 参数对象数组
     * @return 每个操作影响的行数数组
     */
    int[] updateBatch(String commandText, Object... parameters);

    /**
     * 数据库Delete操作
     * @param commandText SQL语句或映射语句名称
     * @return 影响的行数
     */
    int delete(String commandText);

    /**
     * 数据库Delete操作
     * @param commandText SQL语句或映射语句名称
     * @param parameter 参数对象
     * @return 影响的行数
     */
    int delete(String commandText, Object parameter);

    /**
     * 数据库批量Delete操作
     * @param commandText SQL语句或映射语句名称
     * @param parameters 参数对象数组
     * @return 每个操作影响的行数数组
     */
    int[] deleteBatch(String commandText, Object... parameters);

    /**
     * 数据库Get操作
     * @param commandText SQL语句或映射语句名称
     * @return 单个结果对象
     */
    <T> T get(String commandText);

    /**
     * 数据库Get操作
     * @param commandText SQL语句或映射语句名称
     * @param parameter 参数对象
     * @return 单个结果对象
     */
    <T> T get(String commandText, Object parameter);

    /**
     * 数据库Select操作
     * @param commandText SQL语句或映射语句名称
     * @return 结果对象列表
     */
    <T> List<T> select(String commandText);

    /**
     * 数据库Select操作
     * @param commandText SQL语句或映射语句名称
     * @param params 参数对象
     * @return 结果对象列表
     */
    <T> List<T> select(String commandText, Map<String, Object> params);

    /**
     * 数据库分页Select操作
     * @param commandText SQL语句或映射语句名称
     * @param params 参数对象
     * @param page 分页对象
     * @return 结果对象列表
     */
    <T> List<T> select(String commandText, Map<String, Object> params, Page page);
}