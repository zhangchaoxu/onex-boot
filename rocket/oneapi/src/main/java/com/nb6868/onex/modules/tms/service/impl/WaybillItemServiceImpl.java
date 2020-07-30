package com.nb6868.onex.modules.tms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.ConvertUtils;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.tms.dao.WaybillItemDao;
import com.nb6868.onex.modules.tms.dto.WaybillItemDTO;
import com.nb6868.onex.modules.tms.entity.WaybillItemEntity;
import com.nb6868.onex.modules.tms.service.WaybillItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * TMS-运单明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class WaybillItemServiceImpl extends CrudServiceImpl<WaybillItemDao, WaybillItemEntity, WaybillItemDTO> implements WaybillItemService {

    @Override
    public QueryWrapper<WaybillItemEntity> getWrapper(String method, Map<String, Object> params){
        return new WrapperUtils<WaybillItemEntity>(new QueryWrapper<>(), params)
                .like("code", "code")
                .like("sealCode", "seal_code")
                .like("supplierName", "supplier_name")
                .like("goods", "goods")
                .like("goodsType", "goods_type")
                .like("waybillCode", "waybill_code")
                .eq("status", "status")
                .ge("startPurchaseDate", "purchase_date")
                .le("endPurchaseDate", "purchase_date")
                .eq("tenantId", "tenant_id")
                // 数据过滤
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    public List<WaybillItemDTO> getDtoListByWaybillId(Long waybillId) {
        return ConvertUtils.sourceToTarget(query().eq("waybill_id", waybillId).list(), WaybillItemDTO.class);
    }

    @Override
    public boolean deleteByWaybillId(Long waybillId) {
        return logicDeleteByWrapper(new QueryWrapper<WaybillItemEntity>().eq("waybill_id", waybillId));
    }

    @Override
    public boolean unbindByWaybillId(Long waybillId) {
        return update().set("waybill_id", null).set("waybill_code", null).set("status", 0)
                .eq("waybill_id", waybillId).update(new WaybillItemEntity());
    }

    @Override
    public boolean bindByWaybillId(Long waybillItemId, Long waybillId, String waybillCode, int sort) {
        return update().set("waybill_id", waybillId).set("waybill_code", waybillCode).set("sort", sort).set("status", 1)
                .eq("id", waybillItemId).update(new WaybillItemEntity());
    }
}
