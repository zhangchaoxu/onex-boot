package com.nb6868.onex.common;

import cn.hutool.jwt.JWT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@DisplayName("Jwt")
public class JwtTest {

    @Test
    void encode() {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, 30);
        Date expiresDate = nowTime.getTime();

        byte[] key = "1234567890".getBytes();
        String jwtToken = JWT.create()
                .setPayload("sub", "1234567890")
                .setPayload("id", 1118075560757063681L)
                .setPayload("name", "looly")
                .setPayload("admin", true)
                //.setSigner("XXXYYY", key)
                .setKey(key)
                .setExpiresAt(expiresDate)
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .sign();
        System.out.println("jwtToken=" + jwtToken);
    }

    @Test
    void decode() {
        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWQiOjExMTgwNzU1NjA3NTcwNjM2ODEsIm5hbWUiOiJsb29seSIsImFkbWluIjp0cnVlLCJleHAiOjE2NDg1MjQ3MzcsImlhdCI6MTY0ODUyMjkzOCwibmJmIjoxNjQ4NTIyOTM4fQ.kPJCrGFSVWpSicBpbplRH8tqlx-q_LVsLZX4xPyevuU";
        JWT jwt = JWT.of(jwtToken);
        System.out.println("Header=" + jwt.getHeader());
        System.out.println("Payload=" + jwt.getPayload());
        System.out.println("ALGORITHM=" + jwt.getAlgorithm());
        System.out.println("Signer=" + jwt.getSigner());
        // verify只验证内容，不验证时间
        System.out.println("verify=" + jwt.setKey("1234567890".getBytes()).validate(0));
        System.out.println("verify=" + jwt.setKey("1234567890".getBytes()).verify());
    }

}
