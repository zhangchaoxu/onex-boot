package com.nb6868.onex.shop.modules.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.shop.modules.shop.dao.CartDao;
import com.nb6868.onex.shop.modules.shop.dto.CartDTO;
import com.nb6868.onex.shop.modules.shop.entity.CartEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 购物车
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class CartService extends DtoService<CartDao, CartEntity, CartDTO> {

    @Override
    public QueryWrapper<CartEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<CartEntity>(new QueryWrapper<>(), params)
                .eq("id", "id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

}
