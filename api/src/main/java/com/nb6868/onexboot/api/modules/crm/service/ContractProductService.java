package com.nb6868.onexboot.api.modules.crm.service;

import com.nb6868.onexboot.api.modules.crm.dto.ContractProductDTO;
import com.nb6868.onexboot.api.modules.crm.entity.ContractProductEntity;
import com.nb6868.onexboot.common.service.CrudService;

import java.util.List;

/**
 * CRM合同-产品明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ContractProductService extends CrudService<ContractProductEntity, ContractProductDTO> {

    /**
     * 获取合同下的所有产品
     * @param contractId 合同id
     * @return
     */
    List<ContractProductDTO> getDtoListByContractId(Long contractId);

    /**
     * 删除合同下的所有产品
     * @param contractId 合同id
     * @return
     */
    boolean  deleteByContractId(Long contractId);

}
