package com.nb6868.onexboot.api;

import com.nb6868.onexboot.api.modules.sys.dao.TableSchemaDao;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 基本表操作测试
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TableSchemaTest {

    @Resource
    TableSchemaDao tableSchemaDao;

    @Test
    void copy() {
        tableSchemaDao.copyTableStructure("sys_region_bak", "sys_region");
        tableSchemaDao.copyTableData("sys_region_bak", "sys_region");
    }
}
