package com.soonsoft.uranus.data;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.core.model.data.Page;

public interface IDatabaseAccess<TemplateType> {

    int insert(String commandText);

    int insert(String commandText, Object parameter);

    int[] insertBatch(String commandText, Object... parameters);

    int update(String commandText);

    int update(String commandText, Object parameter);

    int[] updateBatch(String commandText, Object... parameters);

    int delete(String commandText);

    int delete(String commandText, Object parameter);

    int[] deleteBatch(String commandText, Object... parameters);

    <T> T get(String commandText);

    <T> T get(String commandText, Object parameter);

    <T> List<T> select(String commandText);

    <T> List<T> select(String commandText, Map<String, Object> params);

    <T> List<T> select(String commandText, Map<String, Object> params, Page page);
}