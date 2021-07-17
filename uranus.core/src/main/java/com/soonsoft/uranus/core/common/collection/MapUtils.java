package com.soonsoft.uranus.core.common.collection;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MapUtils
 */
public abstract class MapUtils {

    /**
     * The default initial capacity of HashMap - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    /**
     * The maximum capacity of HashMap, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor of HashMap used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75F;

    public static <TKey, TValue> HashMap<TKey, TValue> createHashMap() {
        return new HashMap<>(DEFAULT_INITIAL_CAPACITY);
    }

    public static <TKey, TValue> HashMap<TKey, TValue> createHashMap(final int expectedSize) {
        return new HashMap<>(capacity(expectedSize));
    }

    public static <TKey, TValue> LinkedHashMap<TKey, TValue> createLinkedHashMap() {
        return new LinkedHashMap<>(DEFAULT_INITIAL_CAPACITY);
    }

    public static <TKey, TValue> LinkedHashMap<TKey, TValue> createLinkedHashMap(final int expectedSize) {
        return new LinkedHashMap<>(capacity(expectedSize));
    }

    public static <TKey, TValue> boolean isEmpty(Map<TKey, TValue> map) {
        return map == null || map.isEmpty();
    }

    public static <TKey> SimpleMapConverter<TKey> getMapConverter(Map<TKey, Object> map) {
        return new SimpleMapConverter<>(map);
    }

    public static int getCapacity(final int expectedSize) {
        return capacity(expectedSize);
    }

    private static int capacity(final int expectedSize) {
        if(expectedSize < 0) {
            return DEFAULT_INITIAL_CAPACITY;
        }

        if(expectedSize < 3) {
            return expectedSize + 1;
        } else {
            int ft = (int) ((float)expectedSize / DEFAULT_LOAD_FACTOR + 1.0F);
            return ft < MAXIMUM_CAPACITY ? ft : MAXIMUM_CAPACITY;
        }
    }
}