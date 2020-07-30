package com.nb6868.onex.modules.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.uc.dao.BillDao;
import com.nb6868.onex.modules.uc.dto.BillDTO;
import com.nb6868.onex.modules.uc.entity.BillEntity;
import com.nb6868.onex.modules.uc.service.BillService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 账单流水
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class BillServiceImpl extends CrudServiceImpl<BillDao, BillEntity, BillDTO> implements BillService {

    @Override
    public QueryWrapper<BillEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<BillEntity>(new QueryWrapper<>(), params)
                .eq("status", "status")
                .eq("type", "type")
                .eq("userId", "user_id")
                // 创建时间区间
                .ge("startCreateTime", "create_time")
                .le("endCreateTime", "create_time")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

}
