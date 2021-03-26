package com.nb6868.onexboot.api.modules.crm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.crm.dao.ContractDao;
import com.nb6868.onexboot.api.modules.crm.dto.ContractDTO;
import com.nb6868.onexboot.api.modules.crm.dto.ContractProductDTO;
import com.nb6868.onexboot.api.modules.crm.entity.BusinessEntity;
import com.nb6868.onexboot.api.modules.crm.entity.ContractEntity;
import com.nb6868.onexboot.api.modules.crm.entity.CustomerEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.service.UserService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.DtoService;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * CRM合同
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ContractService extends DtoService<ContractDao, ContractEntity, ContractDTO> {

    @Autowired
    CustomerService customerService;
    @Autowired
    UserService userService;
    @Autowired
    ContractProductService contractProductService;
    @Autowired
    ProductService productService;
    @Autowired
    BusinessService businessService;

    @Override
    public QueryWrapper<ContractEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<ContractEntity>(new QueryWrapper<>(), params)
                .eq("customerId", "customer_id")
                .like("name", "name")
                .eq("code", "code")
                .eq("tenantId", "tenant_id")
                .and("search", queryWrapper -> {
                    String search = (String) params.get("search");
                    queryWrapper.like("name", search).or().like("code", search).or();
                })
                .eq("contractYear", "DATE_FORMAT(contract_date, '%Y')")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    protected void beforeSaveOrUpdateDto(ContractDTO dto, ContractEntity toSaveEntity, int type) {
        // 客户名称赋值
        CustomerEntity customer = customerService.getById(dto.getCustomerId());
        AssertUtils.isNull(customer, ErrorCode.ERROR_REQUEST, "客户不存在");
        toSaveEntity.setCustomerName(customer.getName());

        /*UserEntity user = userService.getById(dto.getSalesUserId());
        AssertUtils.isNull(user, ErrorCode.ERROR_REQUEST, "销售人员不存在");
        toSaveEntity.setSalesUserName(user.getRealName());*/
        //销售人员信息冗余
        UserEntity user = userService.getById(dto.getSalesUserId());
        if (!ObjectUtils.isEmpty(user)) {
            toSaveEntity.setSalesUserName(user.getRealName());
        }

        // 控制商机信息是否存在
        if (dto.getBusinessId() != null) {
            BusinessEntity business = businessService.getById(dto.getBusinessId());
            AssertUtils.isNull(business, ErrorCode.ERROR_REQUEST, "商机信息不存在");
            toSaveEntity.setBusinessName(business.getName());
        }
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, ContractDTO dto, ContractEntity existedEntity, int type) {
        if (!ret)
            return;

        // 插入产品
        if (type == 1) {
            // 修改，先删除原先的产品信息
            contractProductService.deleteByContractId(dto.getId());
        }
        // 保存产品
        if (!ObjectUtils.isEmpty(dto.getProductList())) {
            for (ContractProductDTO product : dto.getProductList()) {
                product.setContractId(dto.getId());
                product.setCustomerId(dto.getCustomerId());
                // 清空id,否则会变成保存
                product.setId(null);
                contractProductService.saveDto(product);
            }
        }

        // 添加合同后,客户成交状态标成已成交
        if (0 == type) {
            customerService.update().eq("id", dto.getCustomerId()).eq("deal_state", 0).set("deal_state", 1).update(new CustomerEntity());
        }
    }

    public List<Map<String, Object>> listContractMonthCount(Map<String, Object> params) {
        return getBaseMapper().listContractMonthCount(getWrapper("listContractMonthCount", params));
    }

}
