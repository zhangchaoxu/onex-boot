package com.nb6868.onex.modules.aep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.service.impl.BaseServiceImpl;
import com.nb6868.onex.modules.aep.dao.EnterpriseUserDao;
import com.nb6868.onex.modules.aep.entity.EnterpriseUserEntity;
import com.nb6868.onex.modules.aep.service.EnterpriseUserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AEP-企业用户关联关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class EnterpriseUserServiceImpl extends BaseServiceImpl<EnterpriseUserDao, EnterpriseUserEntity> implements EnterpriseUserService {

    @Override
    public List<EnterpriseUserEntity> getListByEnterpriseId(Long enterpriseId) {
        if (enterpriseId == null) {
            return new ArrayList<>();
        }
        return getBaseMapper().selectList(new QueryWrapper<EnterpriseUserEntity>().eq("enterprise_id", enterpriseId));
    }
}
