package com.nb6868.onex.uc.service;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.exception.OnexException;
import com.pig4cloud.captcha.*;
import com.pig4cloud.captcha.base.Captcha;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 二维码登录服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class QrcodeService {

    @Value("${onex.auth.qrcode-timeout:300000}")
    private long qrcodeTimeout;

    // 定时缓存,有效期默认15分钟
    TimedCache<String, String> qrcodeCache = CacheUtil.newTimedCache(qrcodeTimeout);

    /**
     * 生成二维码
     */
    public void createQrcode(String qrcode) {
        // 保存到缓存
        qrcodeCache.put(qrcode, "none");
    }

    /**
     * 更新二维码
     */
    public void updateQrcode(String qrcode, String value) {
        // 保存到缓存
        qrcodeCache.put(qrcode, value);
    }

    /**
     * 获取二维码
     */
    public String getQrcode(String qrcode) {
        // 保存到缓存
        return qrcodeCache.get(qrcode);
    }

    /**
     * 删除二维码
     */
    public void removeQrcode(String qrcode) {
        // 保存到缓存
        qrcodeCache.remove(qrcode);
    }
}
