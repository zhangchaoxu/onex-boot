package com.nb6868.onex.modules.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.crm.dao.BusinessDao;
import com.nb6868.onex.modules.crm.dto.BusinessDTO;
import com.nb6868.onex.modules.crm.dto.BusinessProductDTO;
import com.nb6868.onex.modules.crm.entity.BusinessEntity;
import com.nb6868.onex.modules.crm.entity.BusinessLogEntity;
import com.nb6868.onex.modules.crm.entity.ContractEntity;
import com.nb6868.onex.modules.crm.entity.CustomerEntity;
import com.nb6868.onex.modules.crm.service.*;
import com.nb6868.onex.modules.uc.entity.UserEntity;
import com.nb6868.onex.modules.uc.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * CRM商机
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class BusinessServiceImpl extends CrudServiceImpl<BusinessDao, BusinessEntity, BusinessDTO> implements BusinessService {

    @Autowired
    CustomerService customerService;
    @Autowired
    ProductService productService;
    @Autowired
    BusinessLogService businessLogService;
    @Autowired
    BusinessProductService businessProductService;
    @Autowired
    UserService userService;
    @Autowired
    ContractService contractService;

    @Override
    public QueryWrapper<BusinessEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<BusinessEntity>(new QueryWrapper<>(), params)
                .eq("customerId", "customer_id")
                .like("name", "name")
                .like("source", "source")
                .eq("status", "status")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    protected void beforeSaveOrUpdateDto(BusinessDTO dto, BusinessEntity toSaveEntity, int type) {
        // 客户名称赋值
        CustomerEntity customer = customerService.getById(dto.getCustomerId());
        AssertUtils.isNull(customer, ErrorCode.ERROR_REQUEST, "客户不存在");
        toSaveEntity.setCustomerName(customer.getName());
        UserEntity user = userService.getById(dto.getSalesUserId());
        if(!ObjectUtils.isEmpty(user)){
            toSaveEntity.setSalesUserName(user.getRealName());
        }
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, BusinessDTO dto, BusinessEntity existedEntity, int type) {
        if (!ret)
            return;

        // 插入日志
        BusinessLogEntity log = new BusinessLogEntity();
        if (type == 0) {
            // 新增
            log.setType("new");
            log.setContent("新增商机");
        } else if (type == 1) {
            // 修改
            log.setType("edit");
            log.setContent("编辑商机");
        }
        log.setBusinessId(dto.getId());
        log.setCustomerId(dto.getCustomerId());
        log.setLogDate(new Date());
        log.setStatus(dto.getStatus());
        businessLogService.save(log);

        // 插入产品
        if (type == 1) {
            // 修改，先删除原先的产品信息
            businessProductService.deleteByBusinessId(dto.getId());
            // 是否修改了名称
            if (!StringUtils.equals(existedEntity.getName(), dto.getName())) {
                // 修改合同中冗余的名字
                contractService.update().eq("business_id", existedEntity.getId()).set("business_name", dto.getName()).update(new ContractEntity());
            }
        }
        // 保存产品
        if (!ObjectUtils.isEmpty(dto.getProductList())) {
            for (BusinessProductDTO product : dto.getProductList()) {
                product.setBusinessId(dto.getId());
                product.setCustomerId(dto.getCustomerId());
                // 清空id,否则会变成更新
                product.setId(null);
                businessProductService.saveDto(product);
            }
        }
    }

    @Override
    public boolean changeStatus(Long id, int newStatus) {
        return update().set("status", newStatus).eq("id", id).update(new BusinessEntity());
    }

    public boolean changeFollowDate(Long id, Date followDate) {
        return update().set("follow_date", followDate).eq("id", id).update(new BusinessEntity());
    }

    @Override
    public List<Map<String, Object>> listStatusCount(Map<String, Object> params) {
        return getBaseMapper().listStatusCount(getWrapper("listStatusCount", params));
    }

}
