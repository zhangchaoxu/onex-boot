package com.nb6868.onex.uc.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.uc.entity.MenuScopeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单权限范围
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface MenuScopeDao extends BaseDao<MenuScopeEntity> {

    /**
     * 查询用户权限列表
     * SELECT
     * 	uc_menu_scope.menu_permissions AS permissions
     * FROM
     * 	uc_menu_scope
     * WHERE
     * 	uc_menu_scope.deleted = 0
     * 	AND uc_menu_scope.menu_permissions != ''
     * 	AND (
     * 		(
     * 			uc_menu_scope.type = 1
     * 			AND uc_menu_scope.role_code IN ( SELECT role_code FROM uc_role_user WHERE uc_role_user.deleted = 0 AND uc_role_user.user_id = 1374288734091931650 )
     * 		)
     * 		OR ( uc_menu_scope.type = 2 AND uc_menu_scope.user_id = 1374288734091931650 )
     * 	)
     * GROUP BY uc_menu_scope.menu_id;
     *
     * @param userId 用户ID
     * @return result
     */
    @Select("SELECT" +
            " uc_menu_scope.menu_permissions AS permissions FROM uc_menu_scope" +
            " WHERE uc_menu_scope.deleted = 0 AND uc_menu_scope.menu_permissions != ''" +
            " AND ((uc_menu_scope.type = 1  AND uc_menu_scope.role_code IN ( SELECT role_code FROM uc_role_user WHERE uc_role_user.deleted = 0 AND uc_role_user.user_id = #{userId})) OR " +
            "(uc_menu_scope.type = 2 AND uc_menu_scope.user_id = #{userId}))" +
            " GROUP BY uc_menu_scope.menu_id")
    List<String> getPermissionsListByUserId(@Param("userId") Long userId);

    /**
     * 查询用户菜单列表
     * @param userId 用户id
     * @return result
     */
    @Select("SELECT" +
            " uc_menu_scope.menu_id AS menu_id FROM uc_menu_scope" +
            " WHERE uc_menu_scope.deleted = 0" +
            " AND ((uc_menu_scope.type = 1  AND uc_menu_scope.role_code IN ( SELECT role_code FROM uc_role_user WHERE uc_role_user.deleted = 0 AND uc_role_user.user_id = #{userId})) OR " +
            "(uc_menu_scope.type = 2 AND uc_menu_scope.user_id = #{userId}))" +
            " GROUP BY uc_menu_scope.menu_id")
    List<Long> getMenuIdListByUserId(@Param("userId") Long userId);

}
