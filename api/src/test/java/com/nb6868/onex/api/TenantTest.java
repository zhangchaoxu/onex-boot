package com.nb6868.onex.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.crm.dao.CustomerDao;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 多租户 Tenant 演示
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TenantTest {

    @Resource
    private CustomerDao mapper;

    @Test
    public void manualSqlTenantFilterTest() {
        System.out.println(mapper.selectCount(new QueryWrapper<>()));
    }

}
