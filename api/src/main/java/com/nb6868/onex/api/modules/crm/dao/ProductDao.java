package com.nb6868.onex.api.modules.crm.dao;

import com.nb6868.onex.api.modules.crm.entity.ProductEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * CRM产品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface ProductDao extends BaseDao<ProductEntity> {

}
