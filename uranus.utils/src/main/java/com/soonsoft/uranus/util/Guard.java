package com.soonsoft.uranus.util;

import java.util.Collection;
import java.util.Map;

import com.soonsoft.uranus.util.collection.CollectionUtils;
import com.soonsoft.uranus.util.collection.MapUtils;
import com.soonsoft.uranus.util.lang.ObjectUtils;
import com.soonsoft.uranus.util.lang.StringUtils;

/**
 * from spring
 */
public abstract class Guard {

    public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
    }
    
    public static void notNull(Object obj, String message) {
        if(obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

	public static void notEmpty(String str, String message) {
        if (StringUtils.isEmpty(str)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Object[] arr, String message) {
        if (ObjectUtils.isEmpty(arr)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> void notEmpty(Collection<T> collections, String message) {
        if (CollectionUtils.isEmpty(collections)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <TKey, TValue> void notEmpty(Map<TKey, TValue> map, String message) {
        if (MapUtils.isEmpty(map)) {
            throw new IllegalArgumentException(message);
        }
    }
    
}