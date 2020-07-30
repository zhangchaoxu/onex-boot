package com.nb6868.onex.modules.uc.dao;

import com.nb6868.onex.booster.dao.BaseDao;
import com.nb6868.onex.modules.uc.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色管理
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Mapper
public interface RoleDao extends BaseDao<RoleEntity> {

    /**
     * 根据用户id查询角色列表
     *
     * @param userId 用户ID
     * @return result
     */
    @Select("SELECT uc_role.code FROM uc_role_user" +
            " LEFT JOIN uc_role ON uc_role.id = uc_role_user.role_id" +
            " WHERE uc_role.deleted = 0 AND uc_role_user.deleted = 0 AND uc_role_user.user_id = #{userId}")
    List<String> getRoleCodeListByUserId(@Param("userId") Long userId);

}
