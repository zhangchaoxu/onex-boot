package com.nb6868.onex.api.modules.crm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.crm.dao.BusinessProductDao;
import com.nb6868.onex.api.modules.crm.dto.BusinessProductDTO;
import com.nb6868.onex.api.modules.crm.entity.BusinessProductEntity;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * CRM商机-产品明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class BusinessProductService extends DtoService<BusinessProductDao, BusinessProductEntity, BusinessProductDTO> {

    @Override
    public QueryWrapper<BusinessProductEntity> getWrapper(String method, Map<String, Object> params){
        return new WrapperUtils<BusinessProductEntity>(new QueryWrapper<>(), params)
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    /**
     * 获取商机下的所有产品
     * @param businessId 商机id
     * @return
     */
    public List<BusinessProductDTO> getDtoListByBusinessId(Long businessId) {
        return ConvertUtils.sourceToTarget(query().eq("business_id", businessId).list(), BusinessProductDTO.class);
    }

    /**
     * 删除商机下的所有产品
     * @param businessId 商机id
     * @return
     */
    public boolean deleteByBusinessId(Long businessId) {
        return logicDeleteByWrapper(new QueryWrapper<BusinessProductEntity>().eq("business_id", businessId));
    }

}
