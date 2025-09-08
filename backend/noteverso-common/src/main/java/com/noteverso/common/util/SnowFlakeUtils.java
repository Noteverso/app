package com.noteverso.common.util;

import static com.noteverso.common.util.IPUtils.getHostAddressWithLong;

public class SnowFlakeUtils {
    private final static long START_TIMESTAMP = 1699163994444L;

    // 每一部分占用的位数
    private final static long SEQUENCE_BIT = 12L; // 序列号
    private final static long WORKER_BIT = 5L;  // 机器标识
    private final static long DATA_CENTER_BIT = 5L; // 数据中心标识

    // 每一部分的最大值
    private final static long MAX_WORKER = ~(-1L << WORKER_BIT);
    private final static long MAX_DATACENTER = ~(-1L << DATA_CENTER_BIT);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    // 每一部分向左的位移
    private final static long WORKER_LEFT = SEQUENCE_BIT; // 12L
    private final static long DATA_CENTER_LEFT = SEQUENCE_BIT + WORKER_BIT; // 17L
    private final static long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT; // 22L

    private long datacenterId = 1L; // 数据中心

    private long workerId = getHostAddressWithLong();

    private long sequence = 0L; // 序列号

    private long lastTimestamp = -1L; // 上一次时间戳

    public SnowFlakeUtils(Long datacenterId, Long workerId) {
        if (datacenterId > MAX_DATACENTER || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER or less than 0");
        }

        if (workerId > MAX_WORKER || workerId < 0) {
            throw new IllegalArgumentException("workerId can't be greater than MAX_WORKER or less than 0");
        }

        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    public synchronized long nextId() {
        // 如果在同一毫秒内，递增序列号
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id for " + (timestamp - lastTimestamp) + " milliseconds");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 如果序列号溢出，等待下一毫秒
            if (sequence == 0) {
                timestamp = getNextMills();
            }
        } else {
            // 不同毫秒内，序列号重置
            sequence = 0L;
        }

        lastTimestamp = timestamp;
        // 生成 64 位ID
        return (timestamp - START_TIMESTAMP) << TIMESTAMP_LEFT
                | datacenterId << DATA_CENTER_LEFT
                | workerId << WORKER_LEFT
                | sequence;
    }

    public long getNextMills() {
        long mills = System.currentTimeMillis();
        while (lastTimestamp <= mills) {
            mills = System.currentTimeMillis();
        }

        return mills;
    }

    public static void main(String[] args) {
        SnowFlakeUtils snowFlakeUtils = new SnowFlakeUtils(1L, 1L);
        System.out.println(snowFlakeUtils.nextId());
    }
}

