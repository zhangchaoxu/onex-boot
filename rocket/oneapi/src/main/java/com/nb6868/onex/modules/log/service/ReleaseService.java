package com.nb6868.onex.modules.log.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.log.dto.ReleaseDTO;
import com.nb6868.onex.modules.log.entity.ReleaseEntity;

/**
 * 更新日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ReleaseService extends CrudService<ReleaseEntity, ReleaseDTO> {


    /**
     * 通过code获取最新的release
     *
     * @param code 查询code
     * @return 结果
     */
    ReleaseDTO getLatestByCode(String code);

}
