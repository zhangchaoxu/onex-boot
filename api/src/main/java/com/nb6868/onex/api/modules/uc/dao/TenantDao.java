package com.nb6868.onex.api.modules.uc.dao;

import com.nb6868.onex.api.modules.uc.entity.TenantEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface TenantDao extends BaseDao<TenantEntity> {

}
