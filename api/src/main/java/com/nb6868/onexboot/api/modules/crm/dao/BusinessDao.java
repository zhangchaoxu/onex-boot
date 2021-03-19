package com.nb6868.onexboot.api.modules.crm.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.crm.entity.BusinessEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * CRM商机
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface BusinessDao extends BaseDao<BusinessEntity> {

    @Select("SELECT crm_business.state, count(crm_business.state) as state_count " +
            "FROM crm_business " +
            "${ew.customSqlSegment} GROUP BY crm_business.state")
    List<Map<String, Object>> listStateCount(@Param(Constants.WRAPPER) Wrapper<BusinessEntity> ew);

}
