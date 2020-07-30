package com.nb6868.onex.modules.aep.service;

import com.nb6868.onex.booster.service.BaseService;
import com.nb6868.onex.modules.aep.entity.EnterpriseUserEntity;

import java.util.List;

/**
 * AEP-企业用户关联关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface EnterpriseUserService extends BaseService<EnterpriseUserEntity> {

    /**
     * 通过企业id获取关联的用户信息
     *
     * @param enterpriseId 企业id
     * @return
     */
    List<EnterpriseUserEntity> getListByEnterpriseId(Long enterpriseId);

}
