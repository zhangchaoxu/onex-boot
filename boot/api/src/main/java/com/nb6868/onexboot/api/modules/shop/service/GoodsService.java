package com.nb6868.onexboot.api.modules.shop.service;

import com.nb6868.onexboot.api.modules.shop.dto.GoodsDTO;
import com.nb6868.onexboot.api.modules.shop.entity.GoodsEntity;
import com.nb6868.onexboot.common.service.CrudService;

import java.math.BigDecimal;

/**
 * 商品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface GoodsService extends CrudService<GoodsEntity, GoodsDTO> {

    /**
     * 修改库存数量
     *
     * @param id 商品Id
     * @param stock 正数添加,负数减少
     */
    boolean addStock(Long id, BigDecimal stock);

}
