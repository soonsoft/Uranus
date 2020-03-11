package com.soonsoft.uranus.util.identity;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * SnowFlakeGenerator
 */
public class SnowFlakeGenerator implements IdentityGenerator<Long> {

    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1480166465631L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12; //序列号占用的位数
    private final static long MACHINE_BIT = 8;   //机器标识占用的位数
    private final static long DATACENTER_BIT = 2;//数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    private final static int MAX_DATACENTER_NUM = ~(-1 << DATACENTER_BIT);
    private final static int MAX_MACHINE_NUM = ~(-1 << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long DATACENTER_LEFT = SEQUENCE_BIT;
    private final static long MACHINE_LEFT = SEQUENCE_BIT + DATACENTER_LEFT;
    private final static long TIMESTMP_LEFT = MACHINE_LEFT + MACHINE_BIT;

    private long datacenterId;  //数据中心
    private long machineId;     //机器标识
    private long sequence = 0L; //序列号
    private long lastStmp = -1L;//上一次时间戳

    public SnowFlakeGenerator() {
        this.datacenterId = 1;
        this.machineId = getMachineId();
    }

    public SnowFlakeGenerator(long datacenterId, long machineId) {
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    @Override
    public Long newID() {
        long currStmp = System.currentTimeMillis();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = nextMillis(lastStmp);
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;

        return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
                | datacenterId << DATACENTER_LEFT       //数据中心部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | sequence;                             //序列号部分
    }

    /**
     * 获取下一不同毫秒的时间戳，不能与最后的时间戳一样
     */
    private static long nextMillis(long lastMillis) {
        long now = System.currentTimeMillis();
        while (now <= lastMillis) {
            now = System.currentTimeMillis();
        }
        return now;
    }

     /**
     * 获取字符串s的字节数组，然后将数组的元素相加，对（max+1）取余
     */
    private static int getHostId(String s, int max){
        byte[] bytes = s.getBytes();
        int sums = 0;
        for(int b : bytes){
            sums += b;
        }
        return sums % (max+1);
    }

    /**
     * 根据 host address 取余，发生异常就获取 0到31之间的随机数
     */
    public static int getMachineId(){
        try {
            return getHostId(Inet4Address.getLocalHost().getHostAddress(), MAX_MACHINE_NUM);
        } catch (UnknownHostException e) {
            return new Random().nextInt(MAX_MACHINE_NUM + 1);
        }
    }
    
}