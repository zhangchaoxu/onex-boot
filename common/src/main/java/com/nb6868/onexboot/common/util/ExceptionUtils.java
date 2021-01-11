package com.nb6868.onexboot.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class ExceptionUtils {

    /**
     * 获取异常的StackTrace
     *
     * @param ex 异常
     * @return 返回异常信息
     */
    public static String getErrorStackTrace(Throwable ex) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw, true);
            ex.printStackTrace(pw);
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
                if (sw != null) {
                    sw.close();
                }
            } catch (Exception ignore) {
                // ignore
            }
        }

        return sw.toString();
    }

}
