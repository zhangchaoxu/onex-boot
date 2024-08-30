package com.nb6868.onex.common.util;

import com.nb6868.onex.common.pojo.ApiResult;

import java.util.function.Supplier;

/**
 * 重试工具类
 * <a href="https://houbb.github.io/2018/08/08/retry">java retry(重试) spring retry, guava retrying 详解</a>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class RetryUtils {

    /**
     * 重试接口，注意若supplier和recover都执行接口调用，则实际会执行maxAttempts+1次
     *
     * @param supplier    执行方法
     * @param recover     降级方法
     * @param maxAttempts 重试次数
     * @param <T>
     * @return 执行结果
     */
    public static <T> ApiResult<T> retryApi(Supplier<ApiResult<T>> supplier, Supplier<ApiResult<T>> recover, int maxAttempts) {
        while (maxAttempts-- > 0) {
            ApiResult<T> result = supplier.get();
            if (result.isSuccess()) {
                return result;
            } else if (!result.isRetry()) {
                // 处理，也不是所有的失败都要重试
                return recover.get();
            } else {
                // 重试
            }
        }
        // 重试次数用完，尝试降级方法
        return recover.get();
    }

    /**
     * 重试接口,返回最后一次执行结果
     *
     * @param supplier    执行方法
     * @param maxAttempts 重试次数
     * @param <T>
     * @return 执行结果
     */
    public static <T> ApiResult<T> retryApi(Supplier<ApiResult<T>> supplier, int maxAttempts) {
        ApiResult<T> laststResult = null;
        while (maxAttempts-- > 0) {
            laststResult = supplier.get();
            if (laststResult.isSuccess() || !laststResult.isRetry()) {
                // 执行结果表示不许需要再重试了
                return laststResult;
            } else {
                // 重试
            }
        }
        return laststResult;
    }

    public static <T> T retryDo(Supplier<T> supplier, Supplier<T> recover, int maxAttempts, Class<? extends Throwable> value) {
        while (maxAttempts-- > 0) {
            try {
                return supplier.get();
            } catch (Exception e) {
                // 判断value
            }
        }
        return recover.get();
    }

}

