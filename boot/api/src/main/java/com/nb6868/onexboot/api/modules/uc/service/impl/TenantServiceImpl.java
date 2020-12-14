package com.nb6868.onexboot.api.modules.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.crm.entity.*;
import com.nb6868.onexboot.api.modules.crm.service.*;
import com.nb6868.onexboot.api.modules.uc.dao.TenantDao;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.api.modules.crm.entity.*;
import com.nb6868.onexboot.api.modules.crm.service.*;
import com.nb6868.onexboot.api.modules.uc.dto.TenantDTO;
import com.nb6868.onexboot.api.modules.uc.entity.TenantEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.service.TenantService;
import com.nb6868.onexboot.api.modules.uc.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TenantServiceImpl extends CrudServiceImpl<TenantDao, TenantEntity, TenantDTO> implements TenantService {

    @Autowired
    UserService userService;
    // crm
    @Autowired
    CustomerService customerService;
    @Autowired
    ProductCategoryService productCategoryService;
    @Autowired
    ProductService productService;
    @Autowired
    BusinessService businessService;
    @Autowired
    ContractService contractService;

    @Override
    public QueryWrapper<TenantEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<TenantEntity>(new QueryWrapper<>(), params)
                .eq("status", "status")
                .like("name", "name")
                .getQueryWrapper();
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, TenantDTO dto, TenantEntity existedEntity, int type) {
        if (1 == type && ret && !StringUtils.equals(existedEntity.getName(), dto.getName())) {
            // 更新成功, name发生变化,更新相关业务表中的code
            userService.update().eq("tenant_id", existedEntity.getId()).set("tenant_name", dto.getName()).update(new UserEntity());
            customerService.update().eq("tenant_id", existedEntity.getId()).set("tenant_name", dto.getName()).update(new CustomerEntity());
            productCategoryService.update().eq("tenant_id", existedEntity.getId()).set("tenant_name", dto.getName()).update(new ProductCategoryEntity());
            productService.update().eq("tenant_id", existedEntity.getId()).set("tenant_name", dto.getName()).update(new ProductEntity());
            businessService.update().eq("tenant_id", existedEntity.getId()).set("tenant_name", dto.getName()).update(new BusinessEntity());
            contractService.update().eq("tenant_id", existedEntity.getId()).set("tenant_name", dto.getName()).update(new ContractEntity());
        }
    }

}
