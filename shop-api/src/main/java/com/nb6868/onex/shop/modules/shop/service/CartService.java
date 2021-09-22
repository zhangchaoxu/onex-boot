package com.nb6868.onex.shop.modules.shop.service;

import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.shop.modules.shop.dao.CartDao;
import com.nb6868.onex.shop.modules.shop.entity.CartEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 购物车
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class CartService extends EntityService<CartDao, CartEntity> {

    /**
     * 计算当前购物车的总费用
     */
    public BigDecimal calcCartTotalPrice() {
        return new BigDecimal(0);
    }

    /**
     * 计算总数
     */
    public Long count(Long userId) {
        return query()
                .eq("state", 1)
                .eq("user_id", userId)
                .count();
    }

}
