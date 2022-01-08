package com.soonsoft.uranus.core.common.collection;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.soonsoft.uranus.core.functional.func.Func1;

public class SimpleMapConverter<TKey> implements IMapConverter<TKey>, Map<TKey, Object> {

    private final static Map<Class<?>, Func1<String, ?>> StringValueCaster = new HashMap<>() {
        {
            put(Boolean.class, v -> Boolean.valueOf(v));
            put(Byte.class, v -> Byte.valueOf(v));
            put(Character.class, v -> v.charAt(0));
            put(Short.class, v -> Short.valueOf(v));
            put(Integer.class, v -> Integer.valueOf(v));
            put(Long.class, v -> Long.valueOf(v));
            put(Float.class, v -> Float.valueOf(v));
            put(Double.class, v -> Double.valueOf(v));
            put(BigDecimal.class, v -> new BigDecimal(v));
            put(String.class, v -> v);
        }

        @Override
        public Func1<String, ?> get(Object key) {
            Func1<String, ?> func = super.get(key);
            if(func != null) {
                return func;
            }
            return __ -> null;
        }
    };

    private final Map<TKey, Object> mapInstance;

    public SimpleMapConverter(Map<TKey, Object> map) {
        if(map == null) {
            throw new IllegalArgumentException("the parameter map is required.");
        }
        this.mapInstance = map;
    }

    //#region IMapConverter

    @Override
    public Boolean getBoolean(TKey key, Boolean defaultValue) {
        return getValue(key, Boolean.class, defaultValue);
    }

    @Override
    public Byte getByte(TKey key, Byte defaultValue) {
        return getValue(key, Byte.class, defaultValue);
    }

    @Override
    public Character getChar(TKey key, Character defaultValue) {
        return getValue(key, Character.class, defaultValue);
    }

    @Override
    public String getString(TKey key, String defaultValue) {
        return getValue(key, String.class, defaultValue);
    }

    @Override
    public Short getShort(TKey key, Short defaultValue) {
        return getValue(key, Short.class, defaultValue);
    }

    @Override
    public Integer getInteger(TKey key, Integer defaultValue) {
        return getValue(key, Integer.class, defaultValue);
    }

    @Override
    public Long getLong(TKey key, Long defaultValue) {
        return getValue(key, Long.class, defaultValue);
    }

    @Override
    public Float getFloat(TKey key, Float defaultValue) {
        return getValue(key, Float.class, defaultValue);
    }

    @Override
    public Double getDouble(TKey key, Double defaultValue) {
        return getValue(key, Double.class, defaultValue);
    }

    @Override
    public BigDecimal getDecimal(TKey key, BigDecimal defaultValue) {
        return getValue(key, BigDecimal.class, defaultValue);
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

    @SuppressWarnings("unchecked")
    protected <T> T getValue(TKey key, Class<T> resultClass, T defaultValue) {
        Object value = mapInstance != null ? mapInstance.get(key) : null;

        if(value == null) {
            return defaultValue;
        }

        if(value instanceof String) {
            return (T) StringValueCaster.get(resultClass).call((String)value);
        }

        return resultClass.cast(value);
    }
    
}
