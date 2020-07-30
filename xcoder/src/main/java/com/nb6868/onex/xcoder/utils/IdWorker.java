package com.nb6868.onex.xcoder.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * 高效GUID产生算法(sequence),基于Snowflake实现64位自增ID算法。 <br>
 * 优化开源项目 http://git.oschina.net/yu120/sequence
 * </p>
 *
 * @author http://git.oschina.net/yu120/sequence
 */
public class IdWorker {

    /**
     * 主机和进程的机器码
     */
    private static Sequence WORKER = new Sequence();

    public static long getId() {
        return WORKER.nextId();
    }

    public static String getIdStr() {
        return String.valueOf(WORKER.nextId());
    }

    /**
     * <p>
     * 有参构造器
     * </p>
     *
     * @param workerId     工作机器 ID
     * @param datacenterId 序列号
     */
    public static void initSequence(long workerId, long datacenterId) {
        WORKER = new Sequence(workerId, datacenterId);
    }

    /**
     * <p>
     * 使用ThreadLocalRandom获取UUID获取更优的效果 去掉"-"
     * </p>
     */
    public static String get32Uuid() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong()).toString().replace(StringPool.DASH, StringPool.EMPTY);
    }

}
