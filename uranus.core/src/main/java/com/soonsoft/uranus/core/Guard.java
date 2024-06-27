package com.soonsoft.uranus.core;

import java.util.Collection;
import java.util.Map;

import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.common.collection.MapUtils;
import com.soonsoft.uranus.core.common.lang.ObjectUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;

/**
 * 参数检查工具集
 */
public abstract class Guard {

    public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
    }

    //#region check null
    
    public static void notNull(Object obj, String message) {
        if(obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void anyNotNull(Object obj1, Object obj2, String message) {
        if(obj1 == null && obj2 == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void anyNotNull(Object obj1, Object obj2, Object obj3, String message) {
        if(obj1 == null && obj2 == null && obj3 == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void allNotNull(Object obj1, Object obj2, String message) {
        if(obj1 == null || obj2 == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void allNotNull(Object obj1, Object obj2, Object obj3, String message) {
        if(obj1 == null || obj2 == null || obj3 == null) {
            throw new IllegalArgumentException(message);
        }
    }

    //#endregion

    //#region check String notEmpty

	public static void notEmpty(String str, String message) {
        if (StringUtils.isEmpty(str)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void anyNotNull(String str1, String str2, String message) {
        if(StringUtils.isEmpty(str1) && StringUtils.isEmpty(str2)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void anyNotNull(String str1, String str2, String str3, String message) {
        if(StringUtils.isEmpty(str1) && StringUtils.isEmpty(str2) && StringUtils.isEmpty(str3)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void allNotNull(String str1, String str2, String message) {
        if(StringUtils.isEmpty(str1) || StringUtils.isEmpty(str2)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void allNotNull(String str1, String str2, String str3, String message) {
        if(StringUtils.isEmpty(str1) || StringUtils.isEmpty(str2) || StringUtils.isEmpty(str3)) {
            throw new IllegalArgumentException(message);
        }
    }

    //#endregion

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