package com.soonsoft.uranus.data.datasource;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.sql.DataSource;

import com.soonsoft.uranus.core.Guard;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * DynamicDataSource
 */ 
public class DynamicDataSource extends AbstractRoutingDataSource {

    private Map<Object, Object> targetDataSources = new HashMap<>();

    private Supplier<Object> keyGetter;

    public DynamicDataSource() {
        this.setTargetDataSources(targetDataSources);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        Guard.notNull(keyGetter, "the keyGetter is required.");
        return keyGetter.get();
    }

    public void put(Object key, DataSource dataSource) {
        targetDataSources.put(key, dataSource);
    }

    public void setKeyGetter(Supplier<Object> getter) {
        this.keyGetter = getter;
    }
}