package com.soonsoft.uranus.core.common.lang;

import java.lang.reflect.Array;
import java.util.Objects;

import com.soonsoft.uranus.core.functional.action.Action3;
import com.soonsoft.uranus.core.functional.behavior.ForEachBehavior;
import com.soonsoft.uranus.core.functional.behavior.IForEach;

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

    public static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    public static ArrayInfo asArrayInfo(Object obj) {
        if(!isArray(obj)) {
            return null;
        }
        return new ArrayInfo(obj);
    }
    
    public static boolean isJava9OrHigher() {
        return getJavaVersion() >= 9;
    }

    public static int getJavaVersion() {
        try {
            Runtime.class.getMethod("version");
            return Runtime.version().feature();
        } catch (NoSuchMethodException e) {
            String version = System.getProperty("java.version");
            if (version.startsWith("1.")) {
                version = version.substring(2, 3);
            } else {
                int dot = version.indexOf(".");
                if (dot != -1) { 
                    version = version.substring(0, dot);
                }
            }
            return Integer.parseInt(version);
        }
    }

    public static class ArrayInfo implements IForEach<Object> {
        private final Object arrayInstance;
        private ArrayInfo(Object obj) {
            this.arrayInstance = obj;
        }

        public int getLength() {
            return Array.getLength(arrayInstance);
        }

        public int getDimensions() {
            int dimensions = 0;
            Class<?> type = arrayInstance.getClass();
            while (type.isArray()) {
                dimensions++;
                type = type.getComponentType();
            }
            return dimensions;
        }

        public Object getValue(int index) {
            return Array.get(arrayInstance, index);
        }

        @Override
        public void forEach(Action3<Object, Integer, ForEachBehavior> action) {
            if(action == null) {
                return;
            }
            int length = getLength();
            ForEachBehavior behavior = new ForEachBehavior();
            for(int i = 0; i < length; i++) {
                behavior.reset();
                action.apply(getValue(i), i, behavior);
                if(behavior.isContinue()) {
                    continue;
                }
                if(behavior.isBreak()) {
                    break;
                }
            }
        }
    }
}