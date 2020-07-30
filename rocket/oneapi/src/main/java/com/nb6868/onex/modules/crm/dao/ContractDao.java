package com.nb6868.onex.modules.crm.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nb6868.onex.booster.dao.BaseDao;
import com.nb6868.onex.modules.crm.entity.ContractEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * CRM合同
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface ContractDao extends BaseDao<ContractEntity> {

    @Select("SELECT DATE_FORMAT(contract_date, '%Y-%m') as contract_month, count(*) as count, sum(amount) as amount_sum " +
            "FROM crm_contract ${ew.customSqlSegment} GROUP BY contract_month")
    List<Map<String, Object>> listContractMonthCount(@Param(Constants.WRAPPER) Wrapper<ContractEntity> ew);

}
