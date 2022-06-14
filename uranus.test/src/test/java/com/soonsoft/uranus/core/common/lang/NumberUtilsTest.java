package com.soonsoft.uranus.core.common.lang;

import org.junit.Test;

public class NumberUtilsTest {

    @Test
    public void test_mapToInt() {
        byte[] values = new byte[] { 1, 2, 3, 8, 10, 15, 31 };

        int value = NumberUtils.mapToInt(values);
        byte[] result = NumberUtils.mapToIntIndexes(value);

        for(int i = 0; i < values.length; i++) {
            assert values[i] == result[i];
        }
    }

    @Test
    public void test_mapToLong() {
        byte[] values = new byte[] { 1, 2, 3, 8, 10, 15, 32, 45, 60, 63 };

        long value = NumberUtils.mapToLong(values);
        byte[] result = NumberUtils.mapToLongIndexes(value);

        for(int i = 0; i < values.length; i++) {
            assert values[i] == result[i];
        }
    }

    @Test
    public void test_radix() {
        int radix = 26;

        int[] numberArr = NumberUtils.convertRadixNumberArray(15625, radix);
        int value = NumberUtils.convertInt(numberArr, radix);

        assert value == 15625;

    }
    
}
