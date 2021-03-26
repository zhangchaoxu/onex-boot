package com.nb6868.onexboot.api.modules.crm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.crm.dao.ContractProductDao;
import com.nb6868.onexboot.api.modules.crm.dto.ContractProductDTO;
import com.nb6868.onexboot.api.modules.crm.entity.ContractProductEntity;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.DtoService;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * CRM合同-产品明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ContractProductService extends DtoService<ContractProductDao, ContractProductEntity, ContractProductDTO> {

    @Override
    public QueryWrapper<ContractProductEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<ContractProductEntity>(new QueryWrapper<>(), params)
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    /**
     * 获取合同下的所有产品
     *
     * @param contractId 合同id
     * @return
     */
    public List<ContractProductDTO> getDtoListByContractId(Long contractId) {
        return ConvertUtils.sourceToTarget(query().eq("contract_id", contractId).list(), ContractProductDTO.class);
    }

    /**
     * 删除合同下的所有产品
     *
     * @param contractId 合同id
     * @return
     */
    public boolean deleteByContractId(Long contractId) {
        return logicDeleteByWrapper(new QueryWrapper<ContractProductEntity>().eq("contract_id", contractId));
    }

}
