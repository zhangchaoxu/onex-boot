package com.nb6868.onexboot.api.modules.log.service;

import com.nb6868.onexboot.api.modules.log.dto.ReleaseDTO;
import com.nb6868.onexboot.api.modules.log.entity.ReleaseEntity;
import com.nb6868.onexboot.common.service.CrudService;

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
