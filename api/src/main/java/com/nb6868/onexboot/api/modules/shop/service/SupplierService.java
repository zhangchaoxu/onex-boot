package com.nb6868.onexboot.api.modules.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.nb6868.onexboot.api.modules.shop.dao.SupplierDao;
import com.nb6868.onexboot.api.modules.shop.dto.SupplierDTO;
import com.nb6868.onexboot.api.modules.shop.entity.SupplierEntity;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.DtoService;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
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
public class SupplierService extends DtoService<SupplierDao, SupplierEntity, SupplierDTO> {

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
