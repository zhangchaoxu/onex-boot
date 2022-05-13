package com.nb6868.onex.uc.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nb6868.onex.uc.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface UserDao extends BaseDao<UserEntity> {

    /**select user with role*/
    String WITH_ROLE_SQL = "select uc_user.*, " +
            "(SELECT uc_dept.name FROM uc_dept WHERE uc_dept.deleted = 0 AND uc_dept.id = uc_user.dept_id) dept_name, role.role_ids, role.role_names" +
            " FROM uc_user" +
            " LEFT JOIN (SELECT GROUP_CONCAT(uc_role_user.role_id) AS role_ids, uc_role_user.user_id, GROUP_CONCAT(uc_role.name) AS role_names FROM uc_role_user LEFT JOIN uc_role ON uc_role_user.role_id = uc_role.id WHERE uc_role_user.deleted = 0 AND uc_role.deleted = 0 GROUP BY uc_role_user.user_id) as role ON role.user_id = uc_user.id" +
            " ${ew.customSqlSegment}";
    @Select(WITH_ROLE_SQL)
    IPage<UserEntity> selectWithRolePage(IPage<UserEntity> page, @Param(Constants.WRAPPER) Wrapper<UserEntity> ew);

    @Select(WITH_ROLE_SQL)
    List<UserEntity> selectWithRoleList(@Param(Constants.WRAPPER) Wrapper<UserEntity> ew);

}
