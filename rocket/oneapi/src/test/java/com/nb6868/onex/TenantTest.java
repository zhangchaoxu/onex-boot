package com.nb6868.onex;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.modules.crm.dao.CustomerDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * 多租户 Tenant 演示
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TenantTest {

    @Resource
    private CustomerDao mapper;

    @Test
    public void manualSqlTenantFilterTest() {
        System.out.println(mapper.selectCount(new QueryWrapper<>()));
    }

}
