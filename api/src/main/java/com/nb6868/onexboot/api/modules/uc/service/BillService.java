package com.nb6868.onexboot.api.modules.uc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.uc.dao.BillDao;
import com.nb6868.onexboot.api.modules.uc.dto.BillDTO;
import com.nb6868.onexboot.api.modules.uc.entity.BillEntity;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.DtoService;
import com.nb6868.onexboot.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 账单流水
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class BillService extends DtoService<BillDao, BillEntity, BillDTO> {

    @Override
    public QueryWrapper<BillEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<BillEntity>(new QueryWrapper<>(), params)
                .eq("state", "state")
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
