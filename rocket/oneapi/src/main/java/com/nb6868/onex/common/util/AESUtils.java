package com.nb6868.onex.common.util;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.exception.OnexException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 加解密工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class AESUtils {

    // 使用AES-128-ECB加密模式，key需要为16位
    private static String KEY = "1234567890adbcde";

    // 参数分别代表 算法名称/加密模式/数据填充方式
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    /**
     * 加密方法
     *
     * @param data 要加密的数据
     * @param key  加密key
     * @return 加密的结果
     */
    public static String encrypt(String data, String key) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));
            byte[] b = cipher.doFinal(data.getBytes("utf-8"));
            // 采用base64算法进行转码,避免出现中文乱码
            return Base64.encodeBase64String(b);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OnexException(ErrorCode.AES_ENCRYPT_ERROR);
        }
    }

    /**
     * 解密方法
     *
     * @param data 要解密的数据
     * @param key  解密key
     * @return 解密的结果
     */
    public static String decrypt(String data, String key) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));
            // 采用base64算法进行转码,避免出现中文乱码
            byte[] encryptBytes = Base64.decodeBase64(data);
            byte[] decryptBytes = cipher.doFinal(encryptBytes);
            return new String(decryptBytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OnexException(ErrorCode.AES_ENCRYPT_ERROR);
        }
    }

    /**
     * 使用默认的key和iv加密
     *
     * @param data
     * @return
     */
    public static String encrypt(String data) {
        return encrypt(data, KEY);
    }

    /**
     * 使用默认的key和iv解密
     *
     * @param data
     * @return
     */
    public static String decrypt(String data) {
        return decrypt(data, KEY);
    }

    /**
     * 测试
     */
    public static void main(String args[]) throws Exception {
        String test1 = "{\"username\":\"1\",\"password\":\"1\",\"uuid\":\"\",\"captcha\":\"\",\"type\":10}";
        String test = new String(test1.getBytes(), "UTF-8");
        String data = null;
        String key = KEY;
        // /g2wzfqvMOeazgtsUVbq1kmJawROa6mcRAzwG1/GeJ4=
        data = encrypt(test, key);
        System.out.println("数据：" + test);
        System.out.println("加密：" + data);
        String jiemi = decrypt(data, key).trim();
        System.out.println("解密：" + jiemi);
    }

}
