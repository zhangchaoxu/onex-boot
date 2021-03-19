package com.nb6868.onexboot.api.modules.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.crm.dao.CustomerDao;
import com.nb6868.onexboot.api.modules.crm.dto.CustomerDTO;
import com.nb6868.onexboot.api.modules.crm.entity.CustomerEntity;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.api.modules.crm.service.CustomerService;
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
                .eq("state", "state")
                .eq("level", "level")
                .like("source", "source")
                .eq("dealState", "deal_state")
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
