package com.nb6868.onexboot.api.modules.uc.dao;

import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.uc.entity.MenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface MenuDao extends BaseDao<MenuEntity> {

    /**
     * 级联用户id
     */
    String JOIN_USER = " LEFT JOIN uc_role_menu ON uc_role_user.role_id = uc_role_menu.role_id" +
            " LEFT JOIN uc_menu ON uc_role_menu.menu_id = uc_menu.id" +
            " WHERE uc_role_user.user_id = #{userId} AND uc_role_user.deleted = 0 AND uc_role_menu.deleted = 0 AND uc_menu.deleted = 0 ";

    /**
     * 查询用户菜单列表
     *
     * @param userId 用户ID
     * @param type   菜单类型
     * @return result
     */
    @Select("<script>" +
            "select uc_menu.* from uc_role_user" + JOIN_USER +
            " <if test='type != null'>" +
            " and uc_menu.type = #{type}" +
            " </if>" +
            "order by uc_menu.sort asc" +
            "</script>")
    List<MenuEntity> getListByUserId(@Param("userId") Long userId, @Param("type") Integer type);

    /**
     * 查询用户权限列表
     *
     * @param userId 用户ID
     * @return result
     */
    @Select("select uc_menu.permissions from uc_role_user" + JOIN_USER + "and uc_menu.permissions != '' order by uc_menu.sort asc")
    List<String> getPermissionsListByUserId(@Param("userId") Long userId);

    /**
     * 查询角色权限列表
     *
     * @param roleCodes 角色列表
     * @return result
     */
    @Select("<script>" +
            "SELECT uc_menu.permissions FROM uc_role_menu" +
            " LEFT JOIN uc_menu ON uc_role_menu.menu_id = uc_menu.id" +
            " WHERE " +
            "<foreach item='item' index='index' collection='roleCodes' open='(' close=')' separator='or'>" +
            "uc_role_menu.role_code = #{item}" +
            "</foreach>" +
            " AND uc_menu.permissions != ''" +
            " AND uc_menu.deleted = 0 AND uc_role_menu.deleted = 0" +
            " ORDER BY uc_menu.sort ASC" +
            "</script>")
    List<String> getPermissionsListByRoles(@Param("roleCodes") List<String> roleCodes);

}
