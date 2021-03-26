package com.nb6868.onexboot.api.modules.crm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.crm.dao.BusinessDao;
import com.nb6868.onexboot.api.modules.crm.dto.BusinessDTO;
import com.nb6868.onexboot.api.modules.crm.dto.BusinessProductDTO;
import com.nb6868.onexboot.api.modules.crm.entity.BusinessEntity;
import com.nb6868.onexboot.api.modules.crm.entity.BusinessLogEntity;
import com.nb6868.onexboot.api.modules.crm.entity.ContractEntity;
import com.nb6868.onexboot.api.modules.crm.entity.CustomerEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.service.UserService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.DtoService;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
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
public class BusinessService extends DtoService<BusinessDao, BusinessEntity, BusinessDTO> {

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
                .eq("state", "state")
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
        log.setState(dto.getState());
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

    /**
     * 修改状态
     * @param id 商机id
     * @param newState 新状态
     * @return
     */
    public boolean changeState(Long id, int newState) {
        return update().set("state", newState).eq("id", id).update(new BusinessEntity());
    }

    /**
     * 修改跟进时间
     * @param id 商机id
     * @param followDate 跟进时间
     * @return
     */
    public boolean changeFollowDate(Long id, Date followDate) {
        return update().set("follow_date", followDate).eq("id", id).update(new BusinessEntity());
    }

    public List<Map<String, Object>> listStateCount(Map<String, Object> params) {
        return getBaseMapper().listStateCount(getWrapper("listStateCount", params));
    }

}
