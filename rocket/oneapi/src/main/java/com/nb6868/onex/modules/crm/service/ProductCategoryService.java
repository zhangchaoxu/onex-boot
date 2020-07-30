package com.nb6868.onex.modules.crm.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.crm.dto.ProductCategoryDTO;
import com.nb6868.onex.modules.crm.entity.ProductCategoryEntity;

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
