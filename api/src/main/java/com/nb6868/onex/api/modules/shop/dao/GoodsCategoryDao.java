package com.nb6868.onex.api.modules.shop.dao;

import com.nb6868.onex.api.modules.shop.entity.GoodsCategoryEntity;
import com.nb6868.onex.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品类别
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface GoodsCategoryDao extends BaseDao<GoodsCategoryEntity> {
    /**
     * 查询所有菜单列表
     *
     */
    @Select("<script>" +
            "select t1.* from shop_category t1" +
            " <where> t1.deleted = 0" +
            " </where>" +
            " order by t1.sort asc" +
            "</script>")
    List<GoodsCategoryEntity> getCategoryList();
}
