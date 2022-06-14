package com.soonsoft.uranus.core.common.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数字处理函数集
 */
public abstract class NumberUtils {
    
    /**
     * 将 byte[] 转换为 int，通过 int 的 bitmap 位数来存储对应的数字
     * 支持 1 ~ 31（java中，最后一位是符号位）
     * @param values
     * @return
     */
    public static int mapToInt(byte[] values) {
        if(values == null || values.length == 0) {
            return 0;
        }
        char[] intBitmap = new char[Integer.SIZE];
        for(int i = 0; i < intBitmap.length; i++) {
            intBitmap[i] = '0';
        }
        for(int i = 0; i < values.length; i++) {
            // 高位转低位
            intBitmap[Integer.SIZE - values[i]] = '1';
        }

        return Integer.parseInt(new String(intBitmap), 2);
    }

    /**
     * 将 int 还原为 byte[]
     * @param value
     * @return
     */
    public static byte[] mapToIntIndexes(int value) {
        if(value == 0) {
            return new byte[0];
        }

        String binaryStr = Integer.toBinaryString(value);
        List<Integer> list = new ArrayList<>(Integer.SIZE);
        for(int i = 0, len = binaryStr.length(); i < len; i++) {
            if(binaryStr.charAt(i) == '1') {
                list.add(len - i);
            }
        }
        
        byte[] result = new byte[list.size()];
        for(int i = 0, j = result.length - 1; i < result.length; i++, j--) {
            result[j] = list.get(i).byteValue();
        }
        return result;
    }

    /**
     * 将 byte[] 转换为 long 
     * 支持 1 ~ 63
     * @param values
     * @return
     */
    public static long mapToLong(byte[] values) {
        if(values == null || values.length == 0) {
            return 0;
        }
        char[] intBitmap = new char[Long.SIZE];
        for(int i = 0; i < intBitmap.length; i++) {
            intBitmap[i] = '0';
        }
        for(int i = 0; i < values.length; i++) {
            // 高位转低位
            intBitmap[Long.SIZE - values[i]] = '1';
        }

        return Long.parseLong(new String(intBitmap), 2);
    }

    /**
     * 将 long 还原为 byte[]
     * @param value
     * @return
     */
    public static byte[] mapToLongIndexes(long value) {
        if(value == 0) {
            return new byte[0];
        }

        String binaryStr = Long.toBinaryString(value);
        List<Long> list = new ArrayList<>(Long.SIZE);
        for(int i = 0, len = binaryStr.length(); i < len; i++) {
            if(binaryStr.charAt(i) == '1') {
                list.add(Long.valueOf(len - i));
            }
        }
        
        byte[] result = new byte[list.size()];
        for(int i = 0, j = result.length - 1; i < result.length; i++, j--) {
            result[j] = list.get(i).byteValue();
        }
        return result;
    }

    /**
     * 转10进制
     * @param numberArr 其他进制
     * @param radix
     * @return
     */
    public static int convertInt(final int[] numberArr, int radix) {
        if(numberArr == null || numberArr.length == 0) {
            return 0;
        }
        int result = 0;
        for(int i = numberArr.length - 1; i >= 0; i--) {
            result += numberArr[i] * Math.pow(radix, numberArr.length - 1 - i);
        }
        return result;
    }

    public static int[] convertRadixNumberArray(int number, int radix) {
        if(number == 0) {
            return new int[] { 0 };
        }
        List<Integer> list = new ArrayList<>(10);
        int value = number;
        while(true) {
            int rest = value / radix;
            list.add(value % radix);

            if(rest == 0) {
                break;
            }

            value = rest;
        }

        Collections.reverse(list);
        return list.stream().mapToInt(Integer::valueOf).toArray();
    }

}
