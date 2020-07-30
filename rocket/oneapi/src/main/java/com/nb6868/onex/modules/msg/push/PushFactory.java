package com.nb6868.onex.modules.msg.push;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.exception.OnexException;

/**
 * PushFactory
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class PushFactory {

    public static AbstractPushService build(PushProps config) {
        // 获取推送配置信息
        if ("jpush".equalsIgnoreCase(config.getPlatform())) {
            return new JPushService();
        } else {
            throw new OnexException(ErrorCode.PUSH_CONFIG_ERROR);
        }
    }
}
