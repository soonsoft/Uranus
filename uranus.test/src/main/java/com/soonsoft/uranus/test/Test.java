package com.soonsoft.uranus.test;

import com.soonsoft.uranus.core.functional.func.Func2;

public class Test {

    public static void main(String[] args) {
        Func2<Integer, Integer, Integer> func = (a, b) -> a + b;
        int result = func.call(1, 2);
        System.out.println(result == 3);

        Object obj = func.call(5, 5);
        System.out.println(obj instanceof Integer);
    }
    
}