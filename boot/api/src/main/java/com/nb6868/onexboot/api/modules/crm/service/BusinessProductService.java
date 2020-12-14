package com.nb6868.onexboot.api.modules.crm.service;

import com.nb6868.onexboot.api.modules.crm.dto.BusinessProductDTO;
import com.nb6868.onexboot.api.modules.crm.entity.BusinessProductEntity;
import com.nb6868.onexboot.common.service.CrudService;

import java.util.List;

/**
 * CRM商机-产品明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface BusinessProductService extends CrudService<BusinessProductEntity, BusinessProductDTO> {

    /**
     * 获取商机下的所有产品
     * @param businessId 商机id
     * @return
     */
    List<BusinessProductDTO> getDtoListByBusinessId(Long businessId);

    /**
     * 删除商机下的所有产品
     * @param businessId 商机id
     * @return
     */
    boolean  deleteByBusinessId(Long businessId);

}
