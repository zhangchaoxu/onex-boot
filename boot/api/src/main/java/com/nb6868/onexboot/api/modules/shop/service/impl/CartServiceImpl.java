package com.nb6868.onexboot.api.modules.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.shop.dao.CartDao;
import com.nb6868.onexboot.api.modules.shop.dto.CartDTO;
import com.nb6868.onexboot.api.modules.shop.entity.CartEntity;
import com.nb6868.onexboot.api.modules.shop.service.CartService;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 购物车
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class CartServiceImpl extends CrudServiceImpl<CartDao, CartEntity, CartDTO> implements CartService {

    @Override
    public QueryWrapper<CartEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<CartEntity>(new QueryWrapper<>(), params)
                .eq("id", "id")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

}
