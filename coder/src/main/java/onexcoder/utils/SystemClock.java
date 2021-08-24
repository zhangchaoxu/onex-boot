package onexcoder.utils;

import java.sql.Timestamp;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

/**
 * 高并发场景下System.currentTimeMillis()的性能问题的优化
 * </p>
 * <p>
 * System.currentTimeMillis()的调用比new一个普通对象要耗时的多（具体耗时高出多少我还没测试过，有人说是100倍左右）<br>
 * System.currentTimeMillis()之所以慢是因为去跟系统打了一次交道<br>
 * 后台定时更新时钟，JVM退出时，线程自动回收<br>
 * 10亿：43410,206,210.72815533980582%<br>
 * 1亿：4699,29,162.0344827586207%<br>
 * 1000万：480,12,40.0%<br>
 * 100万：50,10,5.0%<br>
 *
 * @author Charles (zhangchoxu@gmail.com)
 */
public class SystemClock {

    private final long period;
    private final AtomicLong now;

    private SystemClock(long period) {
        this.period = period;
        this.now = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }

    private static SystemClock instance() {
        return InstanceHolder.INSTANCE;
    }

    public static long now() {
        return instance().currentTimeMillis();
    }

    public static String nowDate() {
        return new Timestamp(instance().currentTimeMillis()).toString();
    }

    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "System Clock");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() -> now.set(System.currentTimeMillis()), period, period, TimeUnit.MILLISECONDS);
    }

    private long currentTimeMillis() {
        return now.get();
    }

    private static class InstanceHolder {
        public static final SystemClock INSTANCE = new SystemClock(1);
    }
}
