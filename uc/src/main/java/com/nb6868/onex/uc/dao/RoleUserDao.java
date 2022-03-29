package com.nb6868.onex.uc.dao;

import com.nb6868.onex.portal.modules.uc.entity.RoleUserEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色用户关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface RoleUserDao extends BaseDao<RoleUserEntity> {

}
