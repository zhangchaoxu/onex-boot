package com.nb6868.onex.modules.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.crm.dao.CustomerDao;
import com.nb6868.onex.modules.crm.dto.CustomerDTO;
import com.nb6868.onex.modules.crm.entity.CustomerEntity;
import com.nb6868.onex.modules.crm.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * CRM客户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class CustomerServiceImpl extends CrudServiceImpl<CustomerDao, CustomerEntity, CustomerDTO> implements CustomerService {

    @Override
    public QueryWrapper<CustomerEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<CustomerEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .eq("status", "status")
                .eq("level", "level")
                .like("source", "source")
                .eq("dealStatus", "deal_status")
                .eq("tenantId", "tenant_id")
                .and("search", queryWrapper -> {
                    String search = (String) params.get("search");
                    queryWrapper.like("name", search).or().like("mobile", search).or().like("telephone", search).or().like("remark", search).or().apply("find_in_set({0}, tags)", search);
                })
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    public List<Map<String, Object>> listSourceCount(Map<String, Object> params) {
        return getBaseMapper().listSourceCount(getWrapper("listSourceCount", params));
    }

}
