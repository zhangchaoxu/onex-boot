package com.nb6868.onex.uc.service;

import com.nb6868.onex.portal.modules.uc.dao.TenantDao;
import com.nb6868.onex.portal.modules.uc.dto.TenantDTO;
import com.nb6868.onex.portal.modules.uc.entity.TenantEntity;
import com.nb6868.onex.common.jpa.DtoService;
import org.springframework.stereotype.Service;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TenantService extends DtoService<TenantDao, TenantEntity, TenantDTO> {

}
