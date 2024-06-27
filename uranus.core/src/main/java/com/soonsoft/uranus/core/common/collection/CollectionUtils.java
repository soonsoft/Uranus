package com.soonsoft.uranus.core.common.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.soonsoft.uranus.core.functional.action.Action3;
import com.soonsoft.uranus.core.functional.behavior.ForEachBehavior;

public abstract class CollectionUtils {

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    //#region isEmpty for Array

    public static boolean isEmpty(String[] stringArray) {
        return stringArray == null || stringArray.length == 0;
    }

    public static boolean isEmpty(int[] intArray) {
        return intArray == null || intArray.length == 0;
    }

    public static boolean isEmpty(long[] longArray) {
        return longArray == null || longArray.length == 0;
    }

    public static boolean isEmpty(float[] floatArray) {
        return floatArray == null || floatArray.length == 0;
    }

    public static boolean isEmpty(double[] doubleArray) {
        return doubleArray == null || doubleArray.length == 0;
    }

    public static boolean isEmpty(boolean[] boolArray) {
        return boolArray == null || boolArray.length == 0;
    }

    public static boolean isEmpty(byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }

    public static boolean isEmpty(char[] charArray) {
        return charArray == null || charArray.length == 0;
    }

    public static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
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

    @SafeVarargs
    public static <T> ArrayList<T> createArrayList(T... elements) {
        ArrayList<T> arrayList = new ArrayList<>();
        if(elements != null) {
            for(int i = 0; i < elements.length; i++) {
                arrayList.add(elements[i]);
            }
        }
        return arrayList;
    }

    public static <T> void foreach(Collection<T> collection, Action3<T, Integer, ForEachBehavior> action) {
        if(action == null) {
            throw new IllegalArgumentException("the arguments action is required.");
        }
        if(!isEmpty(collection)) {
            int index = 0;
            ForEachBehavior behavior = new ForEachBehavior();
            for(T item : collection) {
                behavior.reset();
                action.apply(item, index, behavior);
                if(behavior.isContinue()) {
                    continue;
                }
                if(behavior.isBreak()) {
                    break;
                }
                index++;
            }
        }
    }
}