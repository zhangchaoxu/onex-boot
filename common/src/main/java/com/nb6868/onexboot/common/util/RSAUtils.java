package com.nb6868.onexboot.common.util;

import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA Utils
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class RSAUtils {

    /**
     * 通过字符串生成RSA公钥
     *
     * @param base64s 公钥字符串
     * @return RSA公钥
     */
    public static RSAPublicKey getRSAPublidKeyByBase64(String base64s) {
        try {
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(new Base64().decode(base64s)));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过字符串生成RSA私钥
     *
     * @param base64s 公钥字符串
     * @return RSA私钥
     */
    public static RSAPrivateKey getRSAPrivateKeyByBase64(String base64s) {
        try {
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(new Base64().decode(base64s)));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
