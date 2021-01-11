package com.nb6868.onexboot.api.modules.shop.service;

import com.nb6868.onexboot.api.modules.shop.dto.AppReceiverRequest;
import com.nb6868.onexboot.api.modules.shop.dto.ReceiverDTO;
import com.nb6868.onexboot.api.modules.shop.entity.ReceiverEntity;
import com.nb6868.onexboot.common.service.CrudService;

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
