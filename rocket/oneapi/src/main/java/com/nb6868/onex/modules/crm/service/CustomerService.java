package com.nb6868.onex.modules.crm.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.crm.dto.CustomerDTO;
import com.nb6868.onex.modules.crm.entity.CustomerEntity;

import java.util.List;
import java.util.Map;

/**
 * CRM客户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface CustomerService extends CrudService<CustomerEntity, CustomerDTO> {

    List<Map<String, Object>> listSourceCount(Map<String, Object> params);

}
