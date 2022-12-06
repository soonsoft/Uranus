package com.soonsoft.uranus.core.common.struct.tuple;

public class Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends BaseTuple {
    
    public Tuple9(T1 item1, T2 item2, T3 item3, T4 item4, T5 item5, T6 item6, T7 item7, T8 item8, T9 item9) {
        super(item1, item2, item3, item4, item5, item6, item7, item8, item9);
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

    public T5 getItem5() {
        return get(4);
    }

    public T6 getItem6() {
        return get(5);
    }

    public T7 getItem7() {
        return get(6);
    }

    public T8 getItem8() {
        return get(7);
    }

    public T9 getItem9() {
        return get(8);
    }

}
