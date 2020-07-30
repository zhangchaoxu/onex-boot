package com.nb6868.onex.modules.uc.dao;

import com.nb6868.onex.booster.dao.BaseDao;
import com.nb6868.onex.modules.uc.entity.RoleMenuEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色与菜单对应关系
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Mapper
public interface RoleMenuDao extends BaseDao<RoleMenuEntity> {

}
