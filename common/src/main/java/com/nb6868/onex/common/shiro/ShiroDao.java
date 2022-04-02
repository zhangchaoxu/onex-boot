package com.nb6868.onex.common.shiro;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 授权相关
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface ShiroDao {

    /**
     * 通过id获得用户
     */
    @Select("SELECT * FROM " + ShiroConst.TABLE_USER + " WHERE deleted = 0 AND id = #{id} limit 1")
    Map<String, Object> getUserById(@Param("id") Long id);

    @Select("SELECT type, user_id FROM " + ShiroConst.TABLE_TOKEN + " WHERE deleted = 0 AND token = #{token} AND expire_time > now() limit 1")
    Map<String, Object> getUserTokenByToken(@Param("token") String token);

    /**
     * 更新用户token失效时间
     * @param token token
     * @param expireTime 失效时间(秒)
     */
    @Update("UPDATE " + ShiroConst.TABLE_TOKEN + " SET expire_time = DATE_ADD(NOW(), interval #{expireTime} second) WHERE deleted = 0 AND token = #{token}")
    int updateTokenExpireTime(@Param("token") String token, @Param("expireTime") Integer expireTime);

    /**
     * 获得所有的权限列表
     */
    @Select("SELECT DISTINCT(permissions) FROM " + ShiroConst.TABLE_MENU + " WHERE deleted = 0 AND permissions is not null AND permissions != ''")
    List<String> getAllPermissionsList();

    /**
     * 通过用户id，获得用户权限列表
     * 在menu_scope中的用户权限，叠加该用户角色在menu_scope中的角色权限
     */
    @Select("SELECT " +
            ShiroConst.TABLE_MENU_SCOPE + ".menu_permissions AS permissions FROM " + ShiroConst.TABLE_MENU_SCOPE +
            " WHERE " + ShiroConst.TABLE_MENU_SCOPE + ".deleted = 0 AND " + ShiroConst.TABLE_MENU_SCOPE + ".menu_permissions != '' AND " + ShiroConst.TABLE_MENU_SCOPE + ".menu_permissions is not null" +
            " AND (("+ ShiroConst.TABLE_MENU_SCOPE +".type = 1  AND " + ShiroConst.TABLE_MENU_SCOPE + ".role_code IN " +
            "( SELECT role_code FROM " + ShiroConst.TABLE_USER_ROLE + " WHERE " + ShiroConst.TABLE_USER_ROLE + ".deleted = 0 AND " + ShiroConst.TABLE_USER_ROLE + ".user_id = #{userId})) OR " +
            "(" + ShiroConst.TABLE_MENU_SCOPE + ".type = 2 AND " + ShiroConst.TABLE_MENU_SCOPE + ".user_id = #{userId}))" +
            " GROUP BY " + ShiroConst.TABLE_MENU_SCOPE + ".menu_id")
    List<String> getPermissionsListByUserId(@Param("userId") Long userId);

    /**
     * 获得所有角色列表
     */
    @Select("SELECT DISTINCT(code) FROM " + ShiroConst.TABLE_ROLE + " WHERE deleted = 0")
    List<String> getAllRoleCodeList();

    /**
     * 通过用户id，获得用户角色列表
     */
    @Select("<script>" +
            "SELECT DISTINCT(role_code) FROM " + ShiroConst.TABLE_USER_ROLE + " WHERE user_id = #{userId} AND deleted = 0" +
            "</script>")
    List<String> getRoleCodeListByUserId(@Param("userId") Long userId);

}
