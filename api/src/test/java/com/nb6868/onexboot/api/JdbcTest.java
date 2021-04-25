package com.nb6868.onexboot.api;

import com.nb6868.onexboot.common.util.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * jdbc方法
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JdbcTest {

    @Autowired
    private DbUtils dbUtils;

    @Test
    void readTable() {
        String sql = "select * from ok_geo limit 10";
        List<Map<String, Object>> list = dbUtils.executeQuerySql(sql);
        log.info("list size = " + list.size());
    }
}
