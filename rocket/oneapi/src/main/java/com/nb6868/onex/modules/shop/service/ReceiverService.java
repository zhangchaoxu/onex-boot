package com.nb6868.onex.modules.shop.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.shop.dto.AppReceiverRequest;
import com.nb6868.onex.modules.shop.dto.ReceiverDTO;
import com.nb6868.onex.modules.shop.entity.ReceiverEntity;

/**
 * 收件地址
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ReceiverService extends CrudService<ReceiverEntity, ReceiverDTO> {

    /**
     * 将地址设置为默认项
     *
     * @param id 地址id
     * @return 结果
     */
    boolean setDefaultItem(Long id);

    /**
     * 保存收货地址信息
     *
     * @param id 地址id
     * @return 结果
     */
    boolean saveAppReceiver(AppReceiverRequest request);

    /**
     * 更新收货地址信息
     *
     * @param id 地址id
     * @return 结果
     */
    boolean updateAppReceiver(AppReceiverRequest request);
}
