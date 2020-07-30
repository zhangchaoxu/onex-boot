package com.nb6868.onex.modules.uc.service;

/**
 * 验证码
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface CaptchaService {

    /**
     * 生成图片验证码
     * @param uuid
     * @param width 宽度
     * @param height 高度
     * @param type 类型
     * @return 生成的图片base64
     */
    String createBase64(String uuid, int width, int height, String type);

    /**
     * 效验验证码
     * @param uuid  uuid
     * @param code  验证码
     * @return  true：成功  false：失败
     */
    boolean validate(String uuid, String code);
}
