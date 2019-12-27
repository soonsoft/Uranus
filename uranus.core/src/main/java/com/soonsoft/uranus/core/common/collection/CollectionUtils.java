package com.soonsoft.uranus.core.common.collection;

import java.util.Collection;

/**
 * CollectionUtils
 */
public abstract class CollectionUtils {

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }
}