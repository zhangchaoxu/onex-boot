package com.nb6868.onex.modules.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.shop.dao.CouponDao;
import com.nb6868.onex.modules.shop.dto.CouponDTO;
import com.nb6868.onex.modules.shop.entity.CouponEntity;
import com.nb6868.onex.modules.shop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

/**
 * 优惠券
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class CouponServiceImpl extends CrudServiceImpl<CouponDao, CouponEntity, CouponDTO> implements CouponService {
    @Autowired
    private GoodsCategoryService goodsCategoryService;

    @Autowired
    private GoodsService goodsService;

    @Override
    public QueryWrapper<CouponEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<CouponEntity>(new QueryWrapper<>(), params)
                .like("name", "shop_coupon.name")
                .eq("type", "shop_coupon.type")
                .eq("status", "shop_coupon.status")
                .like("storeName", "shop_store.name")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    public CouponDTO getDtoById(Serializable id) {
        CouponDTO dto = super.getDtoById(id);
        AssertUtils.isEmpty(dto, ErrorCode.RECORD_NOT_EXISTED);

        dto.setLimitGoodsCategoryName(goodsCategoryService.getSelectColumnById(dto.getLimitGoodsCategoryId(), "name"));

        return dto;
    }

}
