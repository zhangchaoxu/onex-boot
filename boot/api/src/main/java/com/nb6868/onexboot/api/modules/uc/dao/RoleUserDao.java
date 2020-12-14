package com.nb6868.onexboot.api.modules.uc.dao;

import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.uc.entity.RoleUserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色用户关系
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Mapper
public interface RoleUserDao extends BaseDao<RoleUserEntity> {

}
