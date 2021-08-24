package com.nb6868.onex.api.modules.uc.dao;

import com.nb6868.onex.api.modules.uc.entity.RoleUserEntity;
import com.nb6868.onex.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色用户关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface RoleUserDao extends BaseDao<RoleUserEntity> {

}
