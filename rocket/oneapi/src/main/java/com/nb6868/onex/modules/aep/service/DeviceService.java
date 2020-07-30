package com.nb6868.onex.modules.aep.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.aep.dto.DeviceDTO;
import com.nb6868.onex.modules.aep.entity.DeviceEntity;
import com.nb6868.onex.modules.aep.entity.ProductEntity;

/**
 * AEP-设备
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface DeviceService extends CrudService<DeviceEntity, DeviceDTO> {

    /**
     * 同步设备数据
     * @return 同步结果
     */
    boolean syncByProduct(ProductEntity product, String searchValue);

    /**
     * 同步设备数据
     * @return 同步结果
     */
    boolean sync(String searchValue);

    /**
     * 通过设备id获得设备
     * @param deviceId 设备ID
     * @return 设备
     */
    DeviceEntity getByDeviceId(String deviceId);

    /**
     * 设备上下线
     * @param deviceId
     * @param status
     * @return 结果
     */
    boolean changeNetStatus(String deviceId, int status);

    /**
     * 设备修改防触电状态
     * @param deviceId
     * @param aepStatus
     * @return 结果
     */
    boolean changeAepStatus(String deviceId, String aepStatus);

    /**
     * 批量删除设备
     * @return
     */
    boolean deleteFromCtwingByDeviceIds(Integer productId, String masterKey, String deviceIds);

    /**
     * 下发指令
     * @return 指令
     */
    boolean sendCommand(String deviceId, String command);

}
