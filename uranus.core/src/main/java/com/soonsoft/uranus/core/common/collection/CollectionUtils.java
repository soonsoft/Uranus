package com.soonsoft.uranus.core.common.collection;

import java.util.Arrays;
import java.util.Collection;

public abstract class CollectionUtils {

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    //#region isEmpty for Array

    public static boolean isEmpty(String[] stringArray) {
        return stringArray == null || stringArray.length == 0;
    }

    //#endregion

    @SafeVarargs
    public static <T> void addAll(Collection<T> collection, T... items) {
        if(collection == null || items == null) {
            return;
        }

        if(items.length > 0) {
            collection.addAll(Arrays.asList(items));
        }
    }
}