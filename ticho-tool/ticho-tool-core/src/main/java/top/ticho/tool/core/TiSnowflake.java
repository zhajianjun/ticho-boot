package top.ticho.tool.core;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-09-13 15:29
 */
public class TiSnowflake {
    private final long workerIdBits = 5L; // 机器ID位数
    private final long datacenterIdBits = 5L; // 数据中心ID位数
    private final long workerId;
    private final long datacenterId;
    private final AtomicLong sequence = new AtomicLong(0L);
    private volatile long lastTimestamp = -1L;

    public TiSnowflake(long workerId, long datacenterId) {
        // 最大机器ID
        long maxWorkerId = ~(-1L << workerIdBits);
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker ID can't be greater than %d or less than 0", maxWorkerId));
        }
        // 最大数据中心ID
        long maxDatacenterId = ~(-1L << datacenterIdBits);
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("Datacenter ID can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public String nextIdStr() {
        return Long.toString(nextId());
    }

    public long nextId() {
        long timestamp = currentTimeMillis();
        long lastTs = lastTimestamp;

        if (timestamp < lastTs) {
            // 等待直到时间追上
            long offset = lastTs - timestamp;
            if (offset <= 5) {
                try {
                    Thread.sleep(offset << 1);
                    timestamp = currentTimeMillis();
                    if (timestamp < lastTs) {
                        throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTs - timestamp));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for next millisecond");
                }
            } else {
                throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTs - timestamp));
            }
        }

        long seq;
        // 序列号位数
        long sequenceBits = 12L;
        if (lastTs == timestamp) {
            // 序列号掩码
            long sequenceMask = ~(-1L << sequenceBits);
            seq = sequence.getAndIncrement() & sequenceMask;
            if (seq == 0) {
                timestamp = tilNextMillis(lastTs);
            }
        } else {
            sequence.set(0L);
            seq = 0L;
        }

        lastTimestamp = timestamp;

        // 自定义起始时间戳
        long twepoch = 1288834974657L;
        // 机器ID左移位数
        // 数据中心ID左移位数
        long datacenterIdShift = sequenceBits + workerIdBits;
        // 时间戳左移位数
        long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
        return ((timestamp - twepoch) << timestampLeftShift) |
            (datacenterId << datacenterIdShift) |
            (workerId << sequenceBits) |
            seq;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }

    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
