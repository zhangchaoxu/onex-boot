package com.nb6868.onex.modules.aep.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.aep.dto.ProductDTO;
import com.nb6868.onex.modules.aep.entity.ProductEntity;

/**
 * AEP-产品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ProductService extends CrudService<ProductEntity, ProductDTO> {

    /**
     * 同步产品数据
     * @return 同步结果
     */
    boolean sync(String searchValue);

    /**
     * 通过产品id获得产品
     * @param productId 产品ID
     * @return 产品
     */
    ProductEntity getByProductId(Integer productId);

}
