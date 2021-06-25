package com.nb6868.onexboot.api;

import cn.hutool.jwt.JWT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * JWT测试
 * see {https://www.hutool.cn/docs/#/jwt/%E6%A6%82%E8%BF%B0}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class JwtTest {

    @Test
    void decode() {
        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiYWRtaW4iOnRydWUsIm5hbWUiOiJsb29seSIsImV4cCI6MTYyNDYxMzU0MzY2N30.N4aWrLQAWd0NeuVxsuivs6wwf_t6EKrYfVIhQWZ5p44";
        JWT jwt = JWT.of(jwtToken);
        System.out.println("Header=" + jwt.getHeader());
        System.out.println("Payload=" + jwt.getPayload());
        System.out.println("ALGORITHM=" + jwt.getAlgorithm());
        System.out.println("Signer=" + jwt.getSigner());
        // verify只验证内容，不验证时间
        System.out.println("verify=" + jwt.setKey("1234567890".getBytes()).verify());
    }


    @Test
    void encode() {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, -30);
        Date expiresDate = nowTime.getTime();

        byte[] key = "1234567890".getBytes();
        String jwtToken = JWT.create()
                .setPayload("sub", "1234567890")
                .setPayload("name", "looly")
                .setPayload("admin", true)
                .setKey(key)
                .setExpiresAt(expiresDate)
                .sign();
        System.out.println("jwtToken=" + jwtToken);
    }

    /*@SneakyThrows
    @Test
    void rsaKeySign() {
        String priKey = "";
        String pubKey = "";
        String uuid = "";
        Date now = DateUtil.date();
        String jwtToken = JWT.create().withAudience()
                .withIssuedAt(now)
                .withExpiresAt(DateUtil.offsetMinute(now, 3))
                .withSubject(uuid)
                .sign(Algorithm.RSA256(RSAUtils.getRSAPublicKeyByBase64(pubKey), RSAUtils.getRSAPrivateKeyByBase64(priKey)));
        log.info("rsa token=\n" + jwtToken);
        // 打开浏览器
        java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://yunmian2020.f3322.net:8000/login?token=" + jwtToken));
    }*/

}
