package com.nb6868.onexboot.api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Junit Test Demo
 * see {https://mp.weixin.qq.com/s/R9sCceBgWXuZXB8q8zYOkQ}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JUnitTest {

    /**
     * 在每个单元测试方法执行前都执行一遍
     */
    @BeforeEach
    void runBeforeEach() {
        System.out.println("runBeforeEach");
    }

    /**
     * 在每个单元测试方法执行前执行一遍（只执行一次）
     */
    @BeforeAll
    static void runBeforeAll() {
        System.out.println("runBeforeAll");
    }

    @Test
    @DisplayName("测试断言equals")
    void testEquals() {
        assertTrue(3 < 4);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("参数化测试")
    void paramTest(int a) {
        assertTrue(a > 0 && a < 4);
    }

}
