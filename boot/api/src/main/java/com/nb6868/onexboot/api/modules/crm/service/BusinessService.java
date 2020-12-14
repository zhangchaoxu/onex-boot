package com.nb6868.onexboot.api.modules.crm.service;

import com.nb6868.onexboot.api.modules.crm.dto.BusinessDTO;
import com.nb6868.onexboot.api.modules.crm.entity.BusinessEntity;
import com.nb6868.onexboot.common.service.CrudService;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * CRM商机
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface BusinessService extends CrudService<BusinessEntity, BusinessDTO> {

    /**
     * 修改状态
     * @param id 商机id
     * @param newStatus 新状态
     * @return
     */
    boolean changeStatus(Long id, int newStatus);

    /**
     * 修改跟进时间
     * @param id 商机id
     * @param followDate 跟进时间
     * @return
     */
    boolean changeFollowDate(Long id, Date followDate);

    List<Map<String, Object>> listStatusCount(Map<String, Object> params);

}
