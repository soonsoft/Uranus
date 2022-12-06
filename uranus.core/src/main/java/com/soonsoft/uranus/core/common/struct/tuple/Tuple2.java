package com.soonsoft.uranus.core.common.struct.tuple;

public class Tuple2<T1, T2> extends BaseTuple {
    
    public Tuple2(T1 item1, T2 item2) {
        super(item1, item2);
    }

    public T1 getItem1() {
        return get(0);
    }

    public T2 getItem2() {
        return get(1);
    }

}
