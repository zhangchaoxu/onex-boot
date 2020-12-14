package com.nb6868.onexboot.api.modules.log.service;

import com.nb6868.onexboot.api.modules.log.dto.LoginDTO;
import com.nb6868.onexboot.common.service.CrudService;
import com.nb6868.onexboot.api.modules.log.entity.LoginEntity;

/**
 * 登录日志
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
public interface LoginService extends CrudService<LoginEntity, LoginDTO> {

}
