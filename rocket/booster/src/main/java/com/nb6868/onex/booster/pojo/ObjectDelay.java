package com.nb6868.onex.booster.pojo;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 实现Delay的Object
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class ObjectDelay<T> implements Delayed {

    /**
     * 业务实体
     */
    private T data;
    /**
     * 到期时间，过期毫秒
     */
    private long expireTime;

    public ObjectDelay(T data, long expireTime) {
        this.data = data;
        this.expireTime = expireTime + Instant.now().toEpochMilli();
    }

    public T getData() {
        return data;
    }

    public long getExpireTime() {
        return expireTime;
    }

    /**
     * 获得剩余时间
     * @param timeUnit 时间单位
     */
    @Override
    public long getDelay(@NotNull TimeUnit timeUnit) {
        return timeUnit.convert(expireTime - Instant.now().toEpochMilli(), timeUnit);
    }

    /**
     * 按照剩余时间排序,剩余时间少的排在前面
     * @param delayed
     * @return
     */
    @Override
    public int compareTo(Delayed delayed) {
        if (delayed == this) {
            return 0;
        }
        long time = getDelay(TimeUnit.NANOSECONDS) - delayed.getDelay(TimeUnit.NANOSECONDS);
        return time == 0 ? 0 : ((time < 0) ? -1 : 1);
    }

}
