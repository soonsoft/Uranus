package com.soonsoft.uranus.data.service.mybatis;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.util.Guard;

import org.mybatis.spring.SqlSessionTemplate;

/**
 * MybatisDatabaseAccess
 */
public class MybatisDatabaseAccess implements IDatabaseAccess {

    private SqlSessionTemplate sqlTemplate;

    /**
     * @return the sqlTemplate
     */
    public SqlSessionTemplate getSqlTemplate() {
        return sqlTemplate;
    }

    /**
     * @param sqlTemplate the sqlTemplate to set
     */
    public void setSqlTemplate(SqlSessionTemplate sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    private void ensureSqlTemplate() {
        Guard.notNull(this.sqlTemplate, "the SqlSessionTemplate is required.");
    }
    
    @Override
    public int insert(String commandText) {
        ensureSqlTemplate();
        return this.sqlTemplate.insert(commandText);
    }

    @Override
    public int insert(String commandText, Object parameter) {
        ensureSqlTemplate();
        return this.sqlTemplate.insert(commandText, parameter);
    }

    @Override
    public int update(String commandText) {
        ensureSqlTemplate();
        return this.sqlTemplate.update(commandText);
    }

    @Override
    public int update(String commandText, Object parameter) {
        ensureSqlTemplate();
        return this.sqlTemplate.update(commandText, parameter);
    }

    @Override
    public int delete(String commandText) {
        ensureSqlTemplate();
        return this.sqlTemplate.delete(commandText);
    }

    @Override
    public int delete(String commandText, Object parameter) {
        ensureSqlTemplate();
        return this.sqlTemplate.delete(commandText, parameter);
    }

    @Override
    public <T> T get(String commandText) {
        ensureSqlTemplate();
        return this.sqlTemplate.selectOne(commandText);
    }

    @Override
    public <T> T get(String commandText, Object parameter) {
        ensureSqlTemplate();
        return this.sqlTemplate.selectOne(commandText, parameter);
    }

    @Override
    public <T> List<T> select(String commandText) {
        ensureSqlTemplate();
        return this.sqlTemplate.selectList(commandText);
    }

    @Override
    public <T> List<T> select(String commandText, Map<String, Object> params) {
        ensureSqlTemplate();
        return this.sqlTemplate.selectList(commandText, params);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public <T> List<T> select(String commandText, Map<String, Object> params, Page page) {
        ensureSqlTemplate();
        Guard.notNull(page, "the Page is required.");

        PageRowBounds rowBounds = new PageRowBounds(page.offset(), page.limit());
        List<Object> result = this.sqlTemplate.selectList(commandText, params, rowBounds);
        page.setTotal(rowBounds.getTotal());
        return (List<T>) result;
    }
    
}