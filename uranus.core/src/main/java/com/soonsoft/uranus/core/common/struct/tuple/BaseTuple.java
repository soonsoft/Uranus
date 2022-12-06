package com.soonsoft.uranus.core.common.struct.tuple;

public abstract class BaseTuple {
    
    private final Object[] items;

    public BaseTuple(Object... items) {
        this.items = items;
    }

    @SuppressWarnings("unchecked")
    protected <R> R get(int index) {
        return (R) items[index];
    }

    protected void set(int index, Object item) {
        items[index] = item;
    }

}
