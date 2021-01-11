package com.nb6868.onexboot.api.modules.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.shop.dao.GoodsDao;
import com.nb6868.onexboot.api.modules.shop.dto.GoodsDTO;
import com.nb6868.onexboot.api.modules.shop.entity.BrandEntity;
import com.nb6868.onexboot.api.modules.shop.entity.GoodsCategoryEntity;
import com.nb6868.onexboot.api.modules.shop.entity.GoodsEntity;
import com.nb6868.onexboot.api.modules.shop.entity.SupplierEntity;
import com.nb6868.onexboot.api.modules.shop.service.BrandService;
import com.nb6868.onexboot.api.modules.shop.service.GoodsCategoryService;
import com.nb6868.onexboot.api.modules.shop.service.GoodsService;
import com.nb6868.onexboot.api.modules.shop.service.SupplierService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 商品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class GoodsServiceImpl extends CrudServiceImpl<GoodsDao, GoodsEntity, GoodsDTO> implements GoodsService {
    @Autowired
    BrandService brandService;
    @Autowired
    SupplierService supplierService;
    @Autowired
    GoodsCategoryService categoryService;

    @Override
    public QueryWrapper<GoodsEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<GoodsEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .eq("sn", "sn")
                .eq("status", "status")
                .eq("top", "top")
                .eq("type", "type")
                .eq("delivery", "delivery")
                .eq("marketable", "marketable")
                .eq("tenantId", "tenant_id")
                .eq("categoryId", "category_id")
                .and("search", queryWrapper -> {
                    String search = (String) params.get("search");
                    queryWrapper.like("name", search).or().like("sn", search).or().like("title", search);
                })
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    protected void beforeSaveOrUpdateDto(GoodsDTO dto, int type) {
        // 检查一下品牌
        if (dto.getBrandId() != null) {
            BrandEntity brand = brandService.getById(dto.getBrandId());
            AssertUtils.isEmpty(brand, ErrorCode.ERROR_REQUEST, "品牌不存在");
        }
        // 检查一下分类
        if (dto.getCategoryId() != null) {
            GoodsCategoryEntity category = categoryService.getById(dto.getCategoryId());
            AssertUtils.isEmpty(category, ErrorCode.ERROR_REQUEST, "分类不存在");
            AssertUtils.isTrue(category.getPid() == 0L, ErrorCode.ERROR_REQUEST, "请选择小类");
        }
        // 检查一下供应商
        if (dto.getSupplierId() != null) {
            SupplierEntity supplier = supplierService.getById(dto.getSupplierId());
            AssertUtils.isEmpty(supplier, ErrorCode.ERROR_REQUEST, "供应商不存在");
        }
        // todo 多规格支持
        if (dto.getSpecType() == 0) {
            // 单规格
            if (dto.getMarketPrice() == null || dto.getSalePrice() == null) {
                throw new OnexException("销售价不允许为空");
            }
        } else {
            // 多规格
            throw new OnexException("目前只支持单规格");
        }
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, GoodsDTO dto, GoodsEntity existedEntity, int type) {

    }

    @Override
    public GoodsDTO getDtoById(Serializable id) {
        GoodsDTO dto = super.getDtoById(id);
        AssertUtils.isNull(dto, ErrorCode.DB_RECORD_NOT_EXISTED);

        SupplierEntity supplier = supplierService.getById(dto.getSupplierId());
        if (supplier != null) {
            dto.setSupplierName(supplier.getName());
        }

        return dto;
    }

    @Override
    public boolean addStock(Long id, BigDecimal stock) {
        return update().eq("id", id).setSql("stock = stock + " + stock).update(new GoodsEntity());
    }

}
