package com.nb6868.onex.api.modules.crm.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nb6868.onex.api.modules.crm.entity.CustomerEntity;
import com.nb6868.onex.common.jpa.BaseDao;
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
