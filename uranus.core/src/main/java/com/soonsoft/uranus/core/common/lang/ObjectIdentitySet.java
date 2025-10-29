package com.soonsoft.uranus.core.common.lang;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 基于对象标识的Set实现，使用System.identityHashCode来避免hashCode()方法的循环引用问题
 */
public class ObjectIdentitySet extends AbstractSet<Object> {
    private final HashMap<IdentityWrapper, Object> map;

    public ObjectIdentitySet() {
        this.map = new HashMap<>();
    }

    @Override
    public boolean add(Object obj) {
        return map.put(new IdentityWrapper(obj), obj) == null;
    }

    @Override
    public boolean remove(Object obj) {
        return map.remove(new IdentityWrapper(obj)) != null;
    }

    @Override
    public boolean contains(Object obj) {
        return map.get(new IdentityWrapper(obj)) == obj;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Iterator<Object> iterator() {
        return map.values().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    /**
     * 使用System.identityHashCode的包装类，避免使用对象自身的hashCode方法
     */
    private static class IdentityWrapper {
        private final Object obj;
        private final int hash;

        public IdentityWrapper(Object obj) {
            this.obj = obj;
            this.hash = System.identityHashCode(obj);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) return true;
            if (!(other instanceof IdentityWrapper)) return false;
            return ((IdentityWrapper) other).obj == this.obj;
        }
    }
}
