package com.nb6868.onex.modules.tms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.tms.dao.WaybillDao;
import com.nb6868.onex.modules.tms.dto.WaybillDTO;
import com.nb6868.onex.modules.tms.dto.WaybillItemDTO;
import com.nb6868.onex.modules.tms.entity.WaybillEntity;
import com.nb6868.onex.modules.tms.service.WaybillItemService;
import com.nb6868.onex.modules.tms.service.WaybillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * TMS-运单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class WaybillServiceImpl extends CrudServiceImpl<WaybillDao, WaybillEntity, WaybillDTO> implements WaybillService {

    @Autowired
    WaybillItemService waybillItemService;

    @Override
    public QueryWrapper<WaybillEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<WaybillEntity>(new QueryWrapper<>(), params)
                .like("code", "code")
                .like("carrierOwner", "carrier_owner")
                .like("sender", "sender")
                .like("carrierName", "carrier_name")
                .like("carrierNo", "carrier_no")
                .eq("tenantId", "tenant_id")
                // 数据过滤
                .apply(Const.SQL_FILTER)
                .getQueryWrapper()
                .eq("deleted", 0);
    }

    @Override
    protected void beforeSaveOrUpdateDto(WaybillDTO dto, WaybillEntity toSaveEntity, int type) {
        // 判断运单号是否已存在
        AssertUtils.isTrue(hasDuplicated(dto.getId(), "code", dto.getCode()), ErrorCode.ERROR_REQUEST, "运单号已存在");
        // 设置运费
        if (dto.getPriceType() == 1) {
            // 全包
            dto.setPrice1(new BigDecimal(0));
            dto.setPrice2(new BigDecimal(0));
            dto.setPrice3(new BigDecimal(0));
            dto.setPrice4(new BigDecimal(0));
            dto.setPrice5(new BigDecimal(0));
            dto.setPrice6(new BigDecimal(0));
            dto.setPrice7(new BigDecimal(0));
            dto.setPrice8(new BigDecimal(0));
            dto.setPrice9(new BigDecimal(0));
        } else if (dto.getPriceType() == 2) {
            // 明细
            dto.setPriceTotal(dto.getPrice1()
                    .add(dto.getPrice2())
                    .add(dto.getPrice3())
                    .add(dto.getPrice4())
                    .add(dto.getPrice5())
                    .add(dto.getPrice6())
                    .add(dto.getPrice7())
                    .add(dto.getPrice8())
                    .add(dto.getPrice9()));
        }
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, WaybillDTO dto, WaybillEntity existedEntity, int type) {
        if (!ret)
            return;

        if (type == 1) {
            // 修改
            // 解绑WaybillItem中该waybill的内容
            waybillItemService.unbindByWaybillId(dto.getId());
        }

        // 绑定item和waybill
        if (!ObjectUtils.isEmpty(dto.getWaybillItemList())) {
            for (int i = 0; i < dto.getWaybillItemList().size(); i++) {
                WaybillItemDTO waybillItem = dto.getWaybillItemList().get(i);
                waybillItem.setWaybillId(dto.getId());
                waybillItem.setWaybillCode(dto.getCode());
                waybillItem.setSort(i);
                waybillItem.setStatus(1);
                waybillItemService.updateDto(waybillItem);
                //waybillItemService.bindByWaybillId(waybillItem.getId(), dto.getId(), dto.getCode(), i);
            }
        }
    }

}
