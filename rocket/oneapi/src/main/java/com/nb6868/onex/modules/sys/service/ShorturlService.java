package com.nb6868.onex.modules.sys.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.sys.dto.ShorturlDTO;
import com.nb6868.onex.modules.sys.entity.ShorturlEntity;

/**
 * 短地址
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ShorturlService extends CrudService<ShorturlEntity, ShorturlDTO> {

    /**
     * 通过code获取短地址
     * @param code
     * @return
     */
    ShorturlEntity getByCode(String code);


}
