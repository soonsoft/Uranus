package com.soonsoft.uranus.test;

import com.soonsoft.uranus.core.functional.func.Func1;

public class JavaLambdaTest {
    
    public static void main(String[] args) {
        Func1<Integer, Integer>[] lambdaArray = createClosure(Integer.valueOf(1000));
        Integer value = lambdaArray[0].call(null);
        lambdaArray[1].call(value);

        System.out.println(lambdaArray[2].call(2));
    }

    @SuppressWarnings("unchecked")
    private static Func1<Integer, Integer>[] createClosure(final Integer value) {
        Func1<Integer, Integer>[] result = new Func1[3];
        
        result[0] = a -> {
            return value;
        };

        result[1] = b -> {
            System.out.println(value == b);
            return b;
        };

        for(final int i = 0; i < 10;) {
            result[2] = c -> {
                return c + i;
            };
            break;
        }

        return result;
    }

}
