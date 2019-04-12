package com.soonsoft.util.collection;

import java.util.Collection;

/**
 * CollectionUtils
 */
public final class CollectionUtils {

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }
}