package com.nb6868.onex.api.modules.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.shop.entity.GoodsEntity;
import com.nb6868.onex.api.modules.shop.dao.StockLogDao;
import com.nb6868.onex.api.modules.shop.dto.StockLogDTO;
import com.nb6868.onex.api.modules.shop.entity.StockLogEntity;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 出入库记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class StockLogService extends DtoService<StockLogDao, StockLogEntity, StockLogDTO> {

    @Autowired
    GoodsService goodsService;

    @Override
    public QueryWrapper<StockLogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<StockLogEntity>(new QueryWrapper<>(), params)
                // 类型
                .eq("type", "type")
                .eq("goodsId", "goods_id")
                // 搜索人
                .eq("createId", "create_id")
                // 创建时间区间
                .ge("startCreateTime", "create_time")
                .le("endCreateTime", "create_time")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    protected void beforeSaveOrUpdateDto(StockLogDTO dto, StockLogEntity toSaveEntity, int type) {
        // 检查商品规格
        GoodsEntity goods = goodsService.getById(dto.getGoodsId());
        AssertUtils.isEmpty(goods, ErrorCode.RECORD_NOT_EXISTED, "商品不存在");

        toSaveEntity.setGoodsId(goods.getId());
        toSaveEntity.setGoodsName(goods.getName());
        toSaveEntity.setTenantId(goods.getTenantId());
        toSaveEntity.setTenantName(goods.getTenantName());

        if (dto.getType() == 0) {
            // 入库
            toSaveEntity.setOutQty(new BigDecimal(0));
            if (dto.getInQty().compareTo(new BigDecimal(0)) < 0) {
                throw new OnexException("入库数量不能小于0");
            }
            // 添加库存
            goodsService.addStock(dto.getGoodsId(), dto.getInQty());
            toSaveEntity.setStock(goods.getStock().add(dto.getInQty()));
        } else if (dto.getType() == 1) {
            // 出库
            toSaveEntity.setInQty(new BigDecimal(0));
            if (dto.getOutQty().compareTo(new BigDecimal(0)) < 0) {
                throw new OnexException("出库数量不能小于0");
            }
            if (goods.getStock().compareTo(dto.getOutQty()) < 0) {
                throw new OnexException("出库数量不能超过当前库存");
            }
            // 减少库存
            goodsService.addStock(dto.getGoodsId(), new BigDecimal(0).subtract(dto.getOutQty()));
            toSaveEntity.setStock(goods.getStock().subtract(dto.getInQty()));
        }
    }

}
