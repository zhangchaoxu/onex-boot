package com.nb6868.onexboot.api.modules.sys.service;

import com.nb6868.onexboot.api.modules.sys.dto.ShorturlDTO;
import com.nb6868.onexboot.api.modules.sys.entity.ShorturlEntity;
import com.nb6868.onexboot.common.service.CrudService;

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
