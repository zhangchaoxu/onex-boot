package com.nb6868.onex.modules.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.crm.dao.ProductDao;
import com.nb6868.onex.modules.crm.dto.ProductDTO;
import com.nb6868.onex.modules.crm.entity.ProductCategoryEntity;
import com.nb6868.onex.modules.crm.entity.ProductEntity;
import com.nb6868.onex.modules.crm.service.ProductCategoryService;
import com.nb6868.onex.modules.crm.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * CRM产品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ProductServiceImpl extends CrudServiceImpl<ProductDao, ProductEntity, ProductDTO> implements ProductService {

    @Autowired
    ProductCategoryService categoryService;

    @Override
    public QueryWrapper<ProductEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<ProductEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .eq("marketable", "marketable")
                .eq("categoryId", "category_id")
                .and("search", queryWrapper -> {
                    String search = (String) params.get("search");
                    queryWrapper.like("name", search).or().like("sn", search).or();
                })
                .apply(Const.SQL_FILTER)
                .getQueryWrapper().orderByAsc("sn");
    }


    @Override
    protected void beforeSaveOrUpdateDto(ProductDTO dto, ProductEntity toSaveEntity, int type) {
        // 检查一下分类
        if (dto.getCategoryId() != null) {
            ProductCategoryEntity category = categoryService.getById(dto.getCategoryId());
            AssertUtils.isNull(category, ErrorCode.ERROR_REQUEST, "分类不存在");
            toSaveEntity.setCategoryName(category.getName());
        }
    }

}
