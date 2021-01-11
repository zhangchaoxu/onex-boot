package com.nb6868.onexboot.api.modules.crm.service;

import com.nb6868.onexboot.api.modules.crm.dto.ProductCategoryDTO;
import com.nb6868.onexboot.api.modules.crm.entity.ProductCategoryEntity;
import com.nb6868.onexboot.common.service.CrudService;

/**
 * CRM 产品类别
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ProductCategoryService extends CrudService<ProductCategoryEntity, ProductCategoryDTO> {

    /**
     * 通过名字获取
     * @param name
     * @return
     */
    ProductCategoryEntity getByName(String name, Long tenantId);

}
