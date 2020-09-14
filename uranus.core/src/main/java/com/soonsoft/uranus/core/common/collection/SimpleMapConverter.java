package com.soonsoft.uranus.core.common.collection;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SimpleMapConverter<TKey> implements IMapConverter<TKey>, Map<TKey, Object> {

    private Map<TKey, Object> mapInstance;

    public SimpleMapConverter(Map<TKey, Object> map) {
        if(map == null) {
            throw new IllegalArgumentException("the parameter map is required.");
        }
        mapInstance = map;
    }

    //#region IMapConverter

    @Override
    public Boolean getBoolean(TKey key, Boolean defaultValue) {
        Object value = getValue(key);

        if(value == null) {
            return defaultValue;
        }

        if(value instanceof String) {
            return Boolean.valueOf((String)value);
        }

        return (Boolean)value;
    }

    @Override
    public Byte getByte(TKey key, Byte defaultValue) {
        Object value = getValue(key);

        if(value == null) {
            return defaultValue;
        }

        if(value instanceof String) {
            return Byte.valueOf((String)value);
        }

        return (Byte)value;
    }

    @Override
    public Character getChar(TKey key, Character defaultValue) {
        Object value = getValue(key);

        if(value == null) {
            return defaultValue;
        }

        return (Character)value;
    }

    @Override
    public String getString(TKey key, String defaultValue) {
        Object value = getValue(key);

        if(value == null) {
            return defaultValue;
        }

        return (String)value;
    }

    @Override
    public Short getShort(TKey key, Short defaultValue) {
        Object value = getValue(key);

        if(value == null) {
            return defaultValue;
        }

        if(value instanceof String) {
            return Short.valueOf((String)value);
        }

        return (Short)value;
    }

    @Override
    public Integer getInteger(TKey key, Integer defaultValue) {
        Object value = getValue(key);

        if(value == null) {
            return defaultValue;
        }

        if(value instanceof String) {
            return Integer.valueOf((String)value);
        }

        return (Integer)value;
    }

    @Override
    public Long getLong(TKey key, Long defaultValue) {
        Object value = getValue(key);

        if(value == null) {
            return defaultValue;
        }

        if(value instanceof String) {
            return Long.valueOf((String)value);
        }

        return (Long)value;
    }

    @Override
    public Float getFloat(TKey key, Float defaultValue) {
        Object value = getValue(key);

        if(value == null) {
            return defaultValue;
        }

        if(value instanceof String) {
            return Float.valueOf((String)value);
        }

        return (Float)value;
    }

    @Override
    public Double getDouble(TKey key, Double defaultValue) {
        Object value = getValue(key);

        if(value == null) {
            return defaultValue;
        }

        if(value instanceof String) {
            return Double.valueOf((String)value);
        }

        return (Double)value;
    }

    @Override
    public BigDecimal getDecimal(TKey key, BigDecimal defaultValue) {
        Object value = getValue(key);

        if(value == null) {
            return defaultValue;
        }

        if(value instanceof String) {
            return new BigDecimal((String)value);
        }

        return (BigDecimal)value;
    }

    //#endregion

    //#region Map

    @Override
    public int size() {
        return mapInstance.size();
    }

    @Override
    public boolean isEmpty() {
        return mapInstance.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return mapInstance.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return mapInstance.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return mapInstance.get(key);
    }

    @Override
    public Object put(TKey key, Object value) {
        return mapInstance.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return mapInstance.remove(key);
    }

    @Override
    public void putAll(Map<? extends TKey, ? extends Object> m) {
        mapInstance.putAll(m);
    }

    @Override
    public void clear() {
        mapInstance.clear();
    }

    @Override
    public Set<TKey> keySet() {
        return mapInstance.keySet();
    }

    @Override
    public Collection<Object> values() {
        return mapInstance.values();
    }

    @Override
    public Set<Entry<TKey, Object>> entrySet() {
        return mapInstance.entrySet();
    }

    //#endregion

    protected Map<TKey, Object> getMap() {
        return mapInstance;
    }

    protected Object getValue(TKey key) {
        return mapInstance != null ? mapInstance.get(key) : null;
    }
    
}
