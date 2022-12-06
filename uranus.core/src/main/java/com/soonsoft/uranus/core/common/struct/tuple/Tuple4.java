package com.soonsoft.uranus.core.common.struct.tuple;

public class Tuple4<T1, T2, T3, T4> extends BaseTuple {
    
    public Tuple4(T1 item1, T2 item2, T3 item3, T4 item4) {
        super(item1, item2, item3, item4);
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

    public T4 getItem4() {
        return get(3);
    }

}
