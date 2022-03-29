package com.nb6868.onex.uc.dao;

import com.nb6868.onex.portal.modules.uc.entity.TenantEntity;
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
