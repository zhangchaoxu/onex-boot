package com.nb6868.onex.modules.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.crm.dao.BusinessLogDao;
import com.nb6868.onex.modules.crm.dto.BusinessLogDTO;
import com.nb6868.onex.modules.crm.entity.BusinessEntity;
import com.nb6868.onex.modules.crm.entity.BusinessLogEntity;
import com.nb6868.onex.modules.crm.service.BusinessLogService;
import com.nb6868.onex.modules.crm.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * CRM商机记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class BusinessLogServiceImpl extends CrudServiceImpl<BusinessLogDao, BusinessLogEntity, BusinessLogDTO> implements BusinessLogService {

    @Autowired
    BusinessService businessService;

    @Override
    public QueryWrapper<BusinessLogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<BusinessLogEntity>(new QueryWrapper<>(), params)
                .like("content", "content")
                .eq("type", "type")
                .eq("customerId", "customer_id")
                .eq("businessId", "business_id")
                // 创建时间区间
                .ge("startCreateTime", "log_date")
                .le("endCreateTime", "log_date")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    protected void beforeSaveOrUpdateDto(BusinessLogDTO dto, BusinessLogEntity toSaveEntity, int type) {
        BusinessEntity business = businessService.getById(dto.getBusinessId());
        AssertUtils.isNull(business, ErrorCode.ERROR_REQUEST, "商机不存在");
        toSaveEntity.setCustomerId(business.getCustomerId());

    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, BusinessLogDTO dto, BusinessLogEntity existedEntity, int type) {
        if (ret) {
            BusinessEntity business = businessService.getById(dto.getBusinessId());
            AssertUtils.isNull(business, ErrorCode.ERROR_REQUEST, "商机不存在");

            // 修改商机状态
            if (!business.getStatus().equals(dto.getStatus())) {
                businessService.changeStatus(business.getId(), dto.getStatus());
            }
            if (dto.getNextFollowDate() != null) {
                businessService.changeFollowDate(business.getId(), dto.getNextFollowDate());
            }
        }
    }

}
