package com.nb6868.onex.modules.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.shop.dao.SupplierDao;
import com.nb6868.onex.modules.shop.dto.SupplierDTO;
import com.nb6868.onex.modules.shop.entity.SupplierEntity;
import com.nb6868.onex.modules.shop.service.GoodsService;
import com.nb6868.onex.modules.shop.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

/**
 * 供应商
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class SupplierServiceImpl extends CrudServiceImpl<SupplierDao, SupplierEntity, SupplierDTO> implements SupplierService {

    @Autowired
    private GoodsService goodsService;

    @Override
    public QueryWrapper<SupplierEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<SupplierEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    public boolean logicDeleteById(Serializable id) {
        AssertUtils.isTrue(SqlHelper.retBool(goodsService.query().eq("supplier_id", id).count()), "店铺存在商品,不允许删除");
        return super.logicDeleteById(id);
    }
}
