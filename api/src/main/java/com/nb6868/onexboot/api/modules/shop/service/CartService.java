package com.nb6868.onexboot.api.modules.shop.service;

import com.nb6868.onexboot.api.modules.shop.dto.CartDTO;
import com.nb6868.onexboot.api.modules.shop.entity.CartEntity;
import com.nb6868.onexboot.common.service.CrudService;

/**
 * 购物车
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface CartService extends CrudService<CartEntity, CartDTO> {

}
