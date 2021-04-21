package com.nb6868.onexboot.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nb6868.onexboot.common.util.DateUtils;
import com.nb6868.onexboot.common.util.RSAUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * JWT测试
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class JwtTest {

    @Test
    void decode() {
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo1LCJvcGVuaWQiOiJvZWoxdTVSdXVXX2hoZUs1UFNXT3d3UGxOOWNnIiwidHlwZSI6MiwiaWF0IjoxNjAwNzM1MTg5LCJleHAiOjE2MDA3NDIzODl9.1YDjgERqLNIWpSbv3h14RRLtxxVDEltIh4sOsoVtqYs";
        // jwt解析identityToken, 获取userIdentifier
        DecodedJWT jwt = JWT.decode(jwtToken);
        int user_id = jwt.getClaim("user_id").asInt();
        String openid = jwt.getClaim("openid").asString();
        int type = jwt.getClaim("type").asInt();
        long exp = jwt.getClaim("exp").asLong();
        Date expireTime = jwt.getExpiresAt();

        boolean isExpired = DateUtils.now().after(expireTime);
        System.out.println("header=" + jwt.getHeader());
        System.out.println("payload=" + jwt.getPayload());
        System.out.println("user_id=" + user_id);
        System.out.println("openid=" + openid);
        System.out.println("type=" + type);
        System.out.println("exp=" + exp);
        System.out.println("过期时间=" + expireTime);
        System.out.println("是否过期=" + isExpired);
    }


    @Test
    void encode() {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, 30);
        Date expiresDate = nowTime.getTime();

        String jwtToken = JWT.create().withAudience("userId")   //签发对象
                .withIssuedAt(new Date())    //发行时间
                .withExpiresAt(expiresDate)  //有效时间
                .withClaim("userName", "userName")    //载荷，随便写几个都可以
                .withClaim("realName", "realName")
                .sign(Algorithm.HMAC256("userId" + "HelloLehr"));
        System.out.println("jwtToken=" + jwtToken);
    }

    /**
     * 使用公钥和私钥RSA签名
     */
    @SneakyThrows
    @Test
    void rsaKeySign() {
        String priKey = "";
        String pubKey = "";
        String uuid = "";
        Date now = new Date();
        String jwtToken = JWT.create().withAudience()
                .withIssuedAt(now)
                .withExpiresAt(DateUtils.addDateMinutes(now, 3))
                .withSubject(uuid)
                .sign(Algorithm.RSA256(RSAUtils.getRSAPublidKeyByBase64(pubKey), RSAUtils.getRSAPrivateKeyByBase64(priKey)));
        log.info("rsa token=\n" + jwtToken);
        // 打开浏览器
        java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://yunmian2020.f3322.net:8000/login?token=" + jwtToken));
    }

}
