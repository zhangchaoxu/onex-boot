package com.nb6868.onex.api;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@DisplayName("Redis")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisTest {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    String key = "testkey";

    @Test
    @DisplayName("设置缓存")
    void setValue() {
        JSONObject value = new JSONObject();
        value.set("jsonKey", "hello world");
        redisTemplate.opsForValue().set(key, value, 1, TimeUnit.MINUTES);
        log.error("写入缓存成功,key={}", key);
    }

    @Test
    @DisplayName("读取缓存")
    void getValue() {
        JSONObject value = (JSONObject)redisTemplate.opsForValue().get(key);
        log.error("读取缓存成功, key={}, value={}", key, JSONUtil.toJsonStr(value));
    }

    @Test
    @DisplayName("设置缓存(String)")
    void setStringValue() {
        JSONObject value = new JSONObject();
        value.set("jsonKey", "hello world");
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), 1, TimeUnit.MINUTES);
        log.error("String写入缓存成功,key={}", key);
    }

    @Test
    @DisplayName("读取缓存(String)")
    void getStringValue() {
        String value = stringRedisTemplate.opsForValue().get(key);
        log.error("String读取缓存成功, key={}, value={}", key, value);
    }

}
