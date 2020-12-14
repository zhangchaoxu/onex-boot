package com.nb6868.onexboot.api.modules.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.crm.dao.BusinessProductDao;
import com.nb6868.onexboot.api.modules.crm.dto.BusinessProductDTO;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.api.modules.crm.entity.BusinessProductEntity;
import com.nb6868.onexboot.api.modules.crm.service.BusinessProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * CRM商机-产品明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class BusinessProductServiceImpl extends CrudServiceImpl<BusinessProductDao, BusinessProductEntity, BusinessProductDTO> implements BusinessProductService {

    @Override
    public QueryWrapper<BusinessProductEntity> getWrapper(String method, Map<String, Object> params){
        return new WrapperUtils<BusinessProductEntity>(new QueryWrapper<>(), params)
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    public List<BusinessProductDTO> getDtoListByBusinessId(Long businessId) {
        return ConvertUtils.sourceToTarget(query().eq("business_id", businessId).list(), BusinessProductDTO.class);
    }

    @Override
    public boolean deleteByBusinessId(Long businessId) {
        return logicDeleteByWrapper(new QueryWrapper<BusinessProductEntity>().eq("business_id", businessId));
    }

}
