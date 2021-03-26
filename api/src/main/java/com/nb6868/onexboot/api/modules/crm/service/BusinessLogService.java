package com.nb6868.onexboot.api.modules.crm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.crm.dao.BusinessLogDao;
import com.nb6868.onexboot.api.modules.crm.dto.BusinessLogDTO;
import com.nb6868.onexboot.api.modules.crm.entity.BusinessEntity;
import com.nb6868.onexboot.api.modules.crm.entity.BusinessLogEntity;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.DtoService;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * CRM商机记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class BusinessLogService extends DtoService<BusinessLogDao, BusinessLogEntity, BusinessLogDTO> {

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
            if (!business.getState().equals(dto.getState())) {
                businessService.changeState(business.getId(), dto.getState());
            }
            if (dto.getNextFollowDate() != null) {
                businessService.changeFollowDate(business.getId(), dto.getNextFollowDate());
            }
        }
    }

}
