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
    @Select("SELECT * FROM " + ShiroConst.TABLE_USER + " WHERE deleted = 0 AND id = #{id} LIMIT 1")
    Map<String, Object> getUserById(@Param("id") Long id);

    /**
     * 通过token value获得token实体
     */
    @Select("SELECT type, user_id FROM " + ShiroConst.TABLE_TOKEN + " WHERE deleted = 0 AND token = #{token} AND expire_time >= now() LIMIT 1")
    Map<String, Object> getUserTokenByToken(@Param("token") String token);

    /**
     * 更新用户token失效时间
     *
     * @param token      token
     * @param expireTime 失效时间(秒)
     */
    @Update("UPDATE " + ShiroConst.TABLE_TOKEN + " SET expire_time = DATE_ADD(NOW(), interval #{expireTime} second) WHERE deleted = 0 AND token = #{token}")
    int updateTokenExpireTime(@Param("token") String token, @Param("expireTime") Integer expireTime);

    /**
     * 获得所有的权限列表
     */
    @Select("<script>" +
            "SELECT DISTINCT(permissions) FROM " + ShiroConst.TABLE_MENU + " WHERE deleted = 0 AND permissions is not null AND permissions != ''" +
            "<choose>" +
            "<when test=\"tenantCode != null and tenantCode != ''\">" +
            " AND tenant_code = #{tenantCode}" +
            "</when>" +
            "<otherwise>" +
            " AND tenant_code IS NULL" +
            "</otherwise>" +
            "</choose>" +
            "</script>")
    List<String> getAllPermissionsList(@Param("tenantCode") String tenantCode);

    /**
     * 通过用户id，获得用户权限列表
     * 在menu_scope中的用户权限，叠加该用户角色在menu_scope中的角色权限
     * <p>
     * SELECT uc_menu_scope.menu_id AS menu_id FROM uc_menu_scope WHERE uc_menu_scope.deleted = 0 AND ((uc_menu_scope.type = 1 AND uc_menu_scope.role_id IN ( SELECT role_id FROM uc_role_user WHERE uc_role_user.deleted = 0 AND uc_role_user.user_id = ?)) OR (uc_menu_scope.type = 2 AND uc_menu_scope.user_id = ?)) GROUP BY uc_menu_scope.menu_id
     */
    @Select("SELECT DISTINCT(" + ShiroConst.TABLE_MENU_SCOPE + ".menu_permissions) AS permissions FROM " + ShiroConst.TABLE_MENU_SCOPE +
            " WHERE " + ShiroConst.TABLE_MENU_SCOPE + ".deleted = 0 AND " + ShiroConst.TABLE_MENU_SCOPE + ".menu_permissions != '' AND " + ShiroConst.TABLE_MENU_SCOPE + ".menu_permissions is not null" +
            " AND ((" + ShiroConst.TABLE_MENU_SCOPE + ".type = 1  AND " + ShiroConst.TABLE_MENU_SCOPE + ".role_id IN " +
            "( SELECT DISTINCT(role_id) FROM " + ShiroConst.TABLE_USER_ROLE + " WHERE " + ShiroConst.TABLE_USER_ROLE + ".deleted = 0 AND " + ShiroConst.TABLE_USER_ROLE + ".user_id = #{userId})) OR " +
            "(" + ShiroConst.TABLE_MENU_SCOPE + ".type = 2 AND " + ShiroConst.TABLE_MENU_SCOPE + ".user_id = #{userId}))")
    List<String> getPermissionsListByUserId(@Param("userId") Long userId);

    /**
     * 通过用户id，获得用户菜单Id列表
     */
    @Select("<script>" +
            "SELECT DISTINCT(" + ShiroConst.TABLE_MENU_SCOPE + ".menu_id) AS menu_id FROM " + ShiroConst.TABLE_MENU_SCOPE +
            " WHERE " + ShiroConst.TABLE_MENU_SCOPE + ".deleted = 0" +
            " AND ((" + ShiroConst.TABLE_MENU_SCOPE + ".type = 1  AND " + ShiroConst.TABLE_MENU_SCOPE + ".role_id IN " +
            "( SELECT DISTINCT(role_id) FROM " + ShiroConst.TABLE_USER_ROLE + " WHERE " + ShiroConst.TABLE_USER_ROLE + ".deleted = 0 AND " + ShiroConst.TABLE_USER_ROLE + ".user_id = #{userId})) OR " +
            "(" + ShiroConst.TABLE_MENU_SCOPE + ".type = 2 AND " + ShiroConst.TABLE_MENU_SCOPE + ".user_id = #{userId}))" +
            "</script>")
    List<Long> getMenuIdListByUserId(@Param("userId") Long userId);

    /**
     * 获得所有角色列表
     */
    @Select("<script>" +
            "SELECT id FROM " + ShiroConst.TABLE_ROLE + " WHERE deleted = 0" +
            "<choose>" +
            "<when test=\"tenantCode != null and tenantCode != ''\">" +
            " AND tenant_code = #{tenantCode}" +
            "</when>" +
            "<otherwise>" +
            " AND tenant_code IS NULL" +
            "</otherwise>" +
            "</choose>" +
            "</script>")
    List<Long> getAllRoleIdList(@Param("tenantCode") String tenantCode);

    /**
     * 获得所有角色编码列表
     */
    @Select("<script>" +
            "SELECT DISTINCT(code) FROM " + ShiroConst.TABLE_ROLE + " WHERE deleted = 0 AND code is not null" +
            "<choose>" +
            "<when test=\"tenantCode != null and tenantCode != ''\">" +
            " AND tenant_code = #{tenantCode}" +
            "</when>" +
            "<otherwise>" +
            " AND tenant_code IS NULL" +
            "</otherwise>" +
            "</choose>" +
            "</script>")
    List<String> getAllRoleCodeList(@Param("tenantCode") String tenantCode);

    /**
     * 通过用户id，获得用户角色列表
     */
    @Select("SELECT DISTINCT(role_id) FROM " + ShiroConst.TABLE_USER_ROLE + " WHERE user_id = #{userId} AND deleted = 0")
    List<Long> getRoleIdListByUserId(@Param("userId") Long userId);

    /**
     * 通过用户id，获得用户角色编码列表
     */
    @Select("SELECT DISTINCT(code) FROM " + ShiroConst.TABLE_ROLE + " WHERE id in (SELECT DISTINCT(role_id) FROM " + ShiroConst.TABLE_USER_ROLE + " WHERE user_id = #{userId} AND deleted = 0) AND code is not null AND deleted = 0")
    List<String> getRoleCodeListByUserId(@Param("userId") Long userId);

}
