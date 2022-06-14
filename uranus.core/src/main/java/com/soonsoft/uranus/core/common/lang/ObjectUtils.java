package com.soonsoft.uranus.core.common.lang;

import java.util.Objects;

/**
 * 对象函数集
 */
public abstract class ObjectUtils {

    public static boolean isEmpty(Object[] array) {
		return (array == null || array.length == 0);
	}

	public static boolean isNull(Object obj) {
		return Objects.isNull(obj);
	}

	public static boolean equals(Object a, Object b) {
		return Objects.equals(a, b);
	}
    
}