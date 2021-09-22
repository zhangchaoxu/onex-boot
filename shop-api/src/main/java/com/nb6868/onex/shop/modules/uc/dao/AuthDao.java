package com.nb6868.onex.shop.modules.uc.dao;

import com.nb6868.onex.shop.modules.uc.UcConst;
import com.nb6868.onex.shop.modules.uc.entity.TokenEntity;
import com.nb6868.onex.shop.modules.uc.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 授权相关
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface AuthDao {

    /**
     * 获得用户角色列表
     */
    @Select("select user_id from " + UcConst.TABLE_USER_ROLE + " where deleted = 0 and user_id = #{userId}")
    List<Long> getUserRolesByUserId(@Param("userId") Long userId);

    /**
     * 获得用户角色列表
     */
    @Select("SELECT" +
            " uc_menu_scope.menu_permissions AS permissions FROM uc_menu_scope" +
            " WHERE uc_menu_scope.deleted = 0 AND uc_menu_scope.menu_permissions != ''" +
            " AND ((uc_menu_scope.type = 1  AND uc_menu_scope.role_id IN ( SELECT role_id FROM uc_role_user WHERE uc_role_user.deleted = 0 AND uc_role_user.user_id = #{userId})) OR " +
            "(uc_menu_scope.type = 2 AND uc_menu_scope.user_id = #{userId}))" +
            " GROUP BY uc_menu_scope.menu_id")
    List<String> getPermissionsListByUserId(@Param("userId") Long userId);

    /**
     * 通过id获得用户
     */
    @Select("select * from " + UcConst.TABLE_USER + " where deleted = 0 and id = #{id} limit 1")
    UserEntity getUserById(@Param("id") Long id);

    @Select("select * from " + UcConst.TABLE_TOKEN + " where deleted = 0 and token = #{token} and expire_time > now() limit 1")
    TokenEntity getUserTokenByToken(@Param("token") String token);

    @Update("update " + UcConst.TABLE_TOKEN + " set expire_time = DATE_ADD(NOW(), interval #{expireTime} second) where deleted = 0 and token = #{token}")
    boolean updateTokenExpireTime(@Param("token") String token, @Param("expireTime") Integer expireTime);
}
