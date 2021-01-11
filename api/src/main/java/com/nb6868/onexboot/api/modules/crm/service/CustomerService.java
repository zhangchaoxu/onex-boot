package com.nb6868.onexboot.api.modules.crm.service;

import com.nb6868.onexboot.api.modules.crm.dto.CustomerDTO;
import com.nb6868.onexboot.api.modules.crm.entity.CustomerEntity;
import com.nb6868.onexboot.common.service.CrudService;

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
