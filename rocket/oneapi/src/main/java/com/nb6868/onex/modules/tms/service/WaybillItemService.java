package com.nb6868.onex.modules.tms.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.tms.dto.WaybillItemDTO;
import com.nb6868.onex.modules.tms.entity.WaybillItemEntity;

import java.util.List;

/**
 * TMS-运单明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface WaybillItemService extends CrudService<WaybillItemEntity, WaybillItemDTO> {
    /**
     * 获取明细数据
     * @param waybillId
     * @return
     */
    List<WaybillItemDTO> getDtoListByWaybillId(Long waybillId);

    /**
     * 解绑运单
     * @param waybillId 运单id
     * @return 结果
     */
    boolean unbindByWaybillId(Long waybillId);

    /**
     * 绑定运单
     */
    boolean bindByWaybillId(Long waybillItemId, Long waybillId, String waybillCode, int sort);

    /**
     * 根据运单ID删除对应明细
     * @param waybillId
     * @return
     */
    boolean deleteByWaybillId(Long waybillId);
}
