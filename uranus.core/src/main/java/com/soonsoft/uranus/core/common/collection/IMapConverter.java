package com.soonsoft.uranus.core.common.collection;

import java.math.BigDecimal;

public interface IMapConverter<TKey> {

    default Boolean getBoolean(TKey key) {
        return getBoolean(key, null);
    }

    Boolean getBoolean(TKey key, Boolean defaultValue);

    default Byte getByte(TKey key) {
        return getByte(key, null);
    }

    Byte getByte(TKey key, Byte defaultValue);

    default Character getChar(TKey key) {
        return getChar(key, null);
    }

    Character getChar(TKey key, Character defaultValue);

    default String getString(TKey key) {
        return getString(key, null);
    }

    String getString(TKey key, String defaultValue);

    default Short getShort(TKey key) {
        return getShort(key, null);
    }

    Short getShort(TKey key, Short defaultValue);

    default Integer getInteger(TKey key) {
        return getInteger(key, null);
    }
    
    Integer getInteger(TKey key, Integer defaultValue);

    default Long getLong(TKey key) {
        return getLong(key, null);
    }

    Long getLong(TKey key, Long defaultValue);

    default Float getFloat(TKey key) {
        return getFloat(key, null);
    }

    Float getFloat(TKey key, Float defaultValue);

    default Double getDouble(TKey key) {
        return getDouble(key, null);
    }

    Double getDouble(TKey key, Double defaultValue);

    default BigDecimal getDecimal(TKey key) {
        return getDecimal(key, null);
    }

    BigDecimal getDecimal(TKey key, BigDecimal defaultValue);

}
