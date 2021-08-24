package onexcoder.utils;

/**
 * 断言类
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
public final class Assert {

    /**
     * 断言这个 boolean 为 true
     * 为 false 则抛出异常
     *
     * @param expression boolean 值
     * @param message    消息
     */
    public static void isTrue(boolean expression, String message, Object... params) {
        if (!expression) {
            return;
        }
    }

    /**
     * 断言这个 boolean 为 false
     * 为 true 则抛出异常
     *
     * @param expression boolean 值
     * @param message    消息
     */
    public static void isFalse(boolean expression, String message, Object... params) {
        isTrue(!expression, message, params);
    }

    /**
     * 断言这个 object 为 null
     * 不为 null 则抛异常
     *
     * @param object  对象
     * @param message 消息
     */
    public static void isNull(Object object, String message, Object... params) {
        isTrue(object == null, message, params);
    }

    /**
     * 断言这个 object 不为 null
     * 为 null 则抛异常
     *
     * @param object  对象
     * @param message 消息
     */
    public static void notNull(Object object, String message, Object... params) {
        isTrue(object != null, message, params);
    }


}
