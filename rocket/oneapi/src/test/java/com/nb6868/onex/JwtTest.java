package com.nb6868.onex;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * JWT测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtTest {

    @Test
    public void decode() {
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo1LCJvcGVuaWQiOiJvZWoxdTVSdXVXX2hoZUs1UFNXT3d3UGxOOWNnIiwidHlwZSI6MiwiaWF0IjoxNjAwNzM1MTg5LCJleHAiOjE2MDA3NDIzODl9.1YDjgERqLNIWpSbv3h14RRLtxxVDEltIh4sOsoVtqYs";
        // jwt解析identityToken, 获取userIdentifier
        DecodedJWT jwt = JWT.decode(jwtToken);
        // audience
        // String packageName = jwt.getAudience().get(0);
        // subject,比如用户id
        String subject = jwt.getSubject();
        // 有效期
        Date expireTime = jwt.getExpiresAt();
        System.out.println("header=" + jwt.getHeader());
        System.out.println("payload=" + jwt.getPayload());
        System.out.println("subject=" + subject);
        System.out.println("expireTime=" + expireTime);
    }

}
