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
    @Select("SELECT * FROM uc_user WHERE deleted = 0 AND id = #{id} LIMIT 1")
    Map<String, Object> getUserById(@Param("id") Long id);

    /**
     * 通过token value获得token实体
     */
    @Select("SELECT type, user_id FROM uc_token WHERE deleted = 0 AND token = #{token} AND expire_time >= now() LIMIT 1")
    Map<String, Object> getUserTokenByToken(@Param("token") String token);

    /**
     * 更新用户token失效时间
     *
     * @param token      token
     * @param expireTime 失效时间(秒)
     */
    @Update(value = "UPDATE uc_token SET expire_time = DATE_ADD(NOW(), interval #{expireTime} second) WHERE deleted = 0 AND token = #{token}", databaseId = "mysql")
    @Update(value = "UPDATE uc_token SET expire_time = now() + #{expireTime} * interval '1 second' WHERE deleted = 0 AND token = #{token}", databaseId = "postgresql")
    int updateTokenExpireTime(@Param("token") String token, @Param("expireTime") Integer expireTime);

    /**
     * 获得所有的权限列表
     */
    @Select("<script>" +
            "SELECT DISTINCT(permissions) FROM uc_menu WHERE deleted = 0 AND permissions is not null AND permissions != ''" +
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
    @Select("SELECT DISTINCT(uc_menu_scope.menu_permissions) AS permissions FROM uc_menu_scope" +
            " WHERE uc_menu_scope.deleted = 0 AND uc_menu_scope.menu_permissions != '' AND uc_menu_scope.menu_permissions is not null" +
            " AND ((uc_menu_scope.type = 1  AND uc_menu_scope.role_id IN " +
            "( SELECT DISTINCT(uc_role_user.role_id) FROM uc_role_user LEFT JOIN uc_role on uc_role_user.role_id = uc_role.id WHERE uc_role_user.user_id = #{userId} AND uc_role.state = 1 AND uc_role.deleted = 0 AND uc_role_user.deleted = 0)) OR " +
            "(uc_menu_scope.type = 2 AND uc_menu_scope.user_id = #{userId}))")
    List<String> getPermissionsListByUserId(@Param("userId") Long userId);

    /**
     * 通过用户id，获得用户菜单Id列表
     */
    @Select("<script>" +
            "SELECT DISTINCT(uc_menu_scope.menu_id) AS menu_id FROM uc_menu_scope" +
            " WHERE uc_menu_scope.deleted = 0" +
            " AND ((uc_menu_scope.type = 1  AND uc_menu_scope.role_id IN " +
            "( SELECT DISTINCT(uc_role_user.role_id) FROM uc_role_user LEFT JOIN uc_role on uc_role_user.role_id = uc_role.id WHERE uc_role_user.user_id = #{userId} AND uc_role.state = 1 AND uc_role.deleted = 0 AND uc_role_user.deleted = 0)) OR " +
            "(uc_menu_scope.type = 2 AND uc_menu_scope.user_id = #{userId}))" +
            "</script>")
    List<Long> getMenuIdListByUserId(@Param("userId") Long userId);

    /**
     * 获得所有角色列表
     */
    @Select("<script>" +
            "SELECT id FROM uc_role WHERE deleted = 0 AND state = 1" +
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
            "SELECT DISTINCT(code) FROM uc_role WHERE deleted = 0 AND state = 1 AND code is not null" +
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
    //@Select("SELECT DISTINCT(role_id) FROM " + ShiroConst.TABLE_USER_ROLE + " WHERE user_id = #{userId} AND deleted = 0")
    @Select("SELECT DISTINCT(id) FROM uc_role WHERE id in (SELECT DISTINCT(role_id) FROM uc_role_user WHERE user_id = #{userId} AND deleted = 0) AND state = 1 AND code is not null AND deleted = 0")
    List<Long> getRoleIdListByUserId(@Param("userId") Long userId);

    /**
     * 通过用户id，获得用户角色编码列表
     */
    @Select("SELECT DISTINCT(code) FROM uc_role WHERE id in (SELECT DISTINCT(role_id) FROM uc_role_user WHERE user_id = #{userId} AND deleted = 0) AND state = 1 AND code is not null AND deleted = 0")
    List<String> getRoleCodeListByUserId(@Param("userId") Long userId);

}
