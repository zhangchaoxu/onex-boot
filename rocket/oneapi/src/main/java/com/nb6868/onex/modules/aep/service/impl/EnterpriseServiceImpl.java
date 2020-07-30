package com.nb6868.onex.modules.aep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.ParamUtils;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.aep.dao.EnterpriseDao;
import com.nb6868.onex.modules.aep.dto.EnterpriseDTO;
import com.nb6868.onex.modules.aep.entity.EnterpriseEntity;
import com.nb6868.onex.modules.aep.service.EnterpriseService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * AEP-企业
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class EnterpriseServiceImpl extends CrudServiceImpl<EnterpriseDao, EnterpriseEntity, EnterpriseDTO> implements EnterpriseService {

    @Override
    public QueryWrapper<EnterpriseEntity> getWrapper(String method, Map<String, Object> params){
        return new WrapperUtils<EnterpriseEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .eq("status", "status")
                .eq("tenantId", "tenant_id")
                .and("search", queryWrapper -> {
                    String search = (String) params.get("search");
                    queryWrapper.like("name", search).or().like("telephone", search).or().like("remark", search).or().apply("find_in_set({0}, tags)", search);
                })
                .apply(Const.SQL_FILTER)
                .getQueryWrapper()
                .eq("deleted", 0)
                .last("list".equalsIgnoreCase(method), "LIMIT " + ParamUtils.toInt(params.get(Const.LIMIT), 1000));
    }

}
