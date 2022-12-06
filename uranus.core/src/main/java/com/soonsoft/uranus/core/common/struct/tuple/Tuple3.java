package com.soonsoft.uranus.core.common.struct.tuple;

public class Tuple3<T1, T2, T3> extends BaseTuple {
    
    public Tuple3(T1 item1, T2 item2, T3 item3) {
        super(item1, item2, item3);
    }

    public T1 getItem1() {
        return get(0);
    }

    public T2 getItem2() {
        return get(1);
    }

    public T3 getItem3() {
        return get(2);
    }

}
