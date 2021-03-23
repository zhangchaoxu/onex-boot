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
     * 级联查询方法
     * SELECT
     * 	uc_menu.*
     * FROM
     * 	uc_role_user
     * 	LEFT JOIN uc_menu_scope ON uc_role_user.role_id = uc_menu_scope.role_id
     * 	LEFT JOIN uc_menu ON uc_menu_scope.menu_id = uc_menu.id
     * WHERE
     * 	uc_role_user.user_id = 1374288734091931650
     * 	AND uc_role_user.deleted = 0
     * 	AND uc_menu_scope.deleted = 0
     * 	AND uc_menu.deleted = 0
     * ORDER BY
     * 	uc_menu.sort ASC
     */

}
