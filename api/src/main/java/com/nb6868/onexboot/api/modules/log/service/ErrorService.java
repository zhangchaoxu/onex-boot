package com.nb6868.onexboot.api.modules.log.service;

import com.nb6868.onexboot.api.modules.log.dto.ErrorDTO;
import com.nb6868.onexboot.api.modules.log.entity.ErrorEntity;
import com.nb6868.onexboot.common.service.CrudService;

/**
 * 异常日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ErrorService extends CrudService<ErrorEntity, ErrorDTO> {

}
