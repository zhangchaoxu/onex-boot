package com.nb6868.onex.api.modules.crm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.crm.dao.CustomerDao;
import com.nb6868.onex.api.modules.crm.dto.CustomerDTO;
import com.nb6868.onex.api.modules.crm.entity.CustomerEntity;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * CRM客户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class CustomerService extends DtoService<CustomerDao, CustomerEntity, CustomerDTO> {

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

    public List<Map<String, Object>> listSourceCount(Map<String, Object> params) {
        return getBaseMapper().listSourceCount(getWrapper("listSourceCount", params));
    }

}
