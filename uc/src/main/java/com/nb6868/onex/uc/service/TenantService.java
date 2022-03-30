package com.nb6868.onex.uc.service;

import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.uc.dao.TenantDao;
import com.nb6868.onex.uc.dto.TenantDTO;
import com.nb6868.onex.uc.entity.TenantEntity;
import org.springframework.stereotype.Service;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TenantService extends DtoService<TenantDao, TenantEntity, TenantDTO> {

}
