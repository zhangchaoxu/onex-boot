package com.nb6868.onex.modules.crm.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nb6868.onex.booster.dao.BaseDao;
import com.nb6868.onex.modules.crm.entity.CustomerEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * CRM客户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface CustomerDao extends BaseDao<CustomerEntity> {

    @Select("SELECT crm_customer.source, count(crm_customer.source) as source_count " +
            "FROM crm_customer " +
            "${ew.customSqlSegment} GROUP BY crm_customer.source")
    List<Map<String, Object>> listSourceCount(@Param(Constants.WRAPPER) Wrapper<CustomerEntity> ew);

}
