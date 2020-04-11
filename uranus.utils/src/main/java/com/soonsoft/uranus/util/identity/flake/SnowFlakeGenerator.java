package com.soonsoft.uranus.util.identity.flake;

import java.util.Date;

import com.soonsoft.uranus.core.common.lang.DateTimeUtils;
import com.soonsoft.uranus.util.identity.IDGenerateException;
import com.soonsoft.uranus.util.identity.IdentityGenerator;

/**
 * SnowFlakeGenerator
 */
public class SnowFlakeGenerator implements IdentityGenerator<Long> {

    /** Bits allocate */
    protected int timestampBits = 41;
    protected int workerIdBits = 10;
    protected int sequenceBits = 12;

    // 纪念2019-nCoV新冠状病毒，武汉封城的日子
    protected String epochStr = "2020-01-23";
    protected long epochMilliseconds = 1582387200000L;

    protected BitsAllocator bitsAllocator;
    protected long workerId;

    protected long sequence = 0L;
    protected long lastMilliseconds = -1L;

    public void initialize(long workerId) {
        bitsAllocator = new BitsAllocator(timestampBits, workerIdBits, sequenceBits);
        this.workerId = workerId;
        if(workerId > bitsAllocator.getMaxWorkerId()) {
            throw new RuntimeException("Worker id " + workerId + " exceeds the max " + bitsAllocator.getMaxWorkerId());
        }
    }

    @Override
    public Long newID() {
        try {
            return nextId();
        } catch(Exception e) {
            throw new IDGenerateException(e);
        }
    }
    
    public String parse(long uid) {
        long totalBits = BitsAllocator.TOTAL_BITS;
        long signBits = bitsAllocator.getSignBits();
        long timestampBits = bitsAllocator.getTimestampBits();
        long workerIdBits = bitsAllocator.getWorkerIdBits();
        long sequenceBits = bitsAllocator.getSequenceBits();

        // parse UID
        long sequence = (uid << (totalBits - sequenceBits)) >>> (totalBits - sequenceBits);
        long workerId = (uid << (timestampBits + signBits)) >>> (totalBits - workerIdBits);
        long deltaSeconds = uid >>> (workerIdBits + sequenceBits);

        Date thatTime = new Date(epochMilliseconds + deltaSeconds);
        String thatTimeStr = DateTimeUtils.formatWithMillis(thatTime);

        // format as string
        return String.format("{\"UID\":\"%d\",\"timestamp\":\"%s\",\"workerId\":\"%d\",\"sequence\":\"%d\"}",
                uid, thatTimeStr, workerId, sequence);
    }

    protected synchronized long nextId() {
        long currentMilliseconds = getCurrentMilliseconds();

        // Clock moved backwards, refuse to generate uid
        if (currentMilliseconds < lastMilliseconds) {
            long refusedSeconds = lastMilliseconds - currentMilliseconds;
            throw new IDGenerateException("Clock moved backwards. Refusing for %d seconds", refusedSeconds);
        }

        // At the same second, increase sequence
        if (currentMilliseconds == lastMilliseconds) {
            sequence = (sequence + 1) & bitsAllocator.getMaxSequence();
            // Exceed the max sequence, we wait the next second to generate uid
            if (sequence == 0) {
                currentMilliseconds = getNextMilliseconds(lastMilliseconds);
            }

        // At the different second, sequence restart from zero
        } else {
            sequence = 0L;
        }

        lastMilliseconds = currentMilliseconds;

        // Allocate bits for UID
        return bitsAllocator.allocate(currentMilliseconds - epochMilliseconds, workerId, sequence);
    }

    private long getNextMilliseconds(long lastTimestamp) {
        long timestamp = getCurrentMilliseconds();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentMilliseconds();
        }
        return timestamp;
    }

    private long getCurrentMilliseconds() {
        long currentMilliseconds = System.currentTimeMillis();
        if (currentMilliseconds - epochMilliseconds > bitsAllocator.getMaxDeltaSeconds()) {
            throw new IDGenerateException("Timestamp bits is exhausted. Refusing UID generate. Now: " + currentMilliseconds);
        }
        return currentMilliseconds;
    }

}