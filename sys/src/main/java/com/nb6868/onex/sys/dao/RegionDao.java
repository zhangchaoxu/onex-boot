package com.nb6868.onex.sys.dao;

import com.nb6868.onex.sys.entity.RegionEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface RegionDao extends BaseDao<RegionEntity> {

    @Select("SELECT id AS adcode, deep, ext_name AS township," +
            " ( SELECT ext_name FROM sys_region AS d WHERE d.id = sys_region.pid ) AS district," +
            " ( SELECT ext_name FROM sys_region AS c WHERE c.id = (SELECT pid FROM sys_region AS d WHERE d.id = sys_region.pid) ) AS city," +
            " ( SELECT ext_name FROM sys_region AS p WHERE p.id = (SELECT pid FROM sys_region AS c WHERE c.id = (SELECT pid FROM sys_region AS d WHERE d.id = sys_region.pid)) ) AS province" +
            " FROM sys_region WHERE id = #{id}")
    Map<String, Object> getPcdtByT(@Param("id") Long id);

    @Select("SELECT id AS adcode, deep, ext_name AS district," +
            " ( SELECT ext_name FROM sys_region AS c WHERE c.id = sys_region.pid ) AS city," +
            " ( SELECT ext_name FROM sys_region AS p WHERE p.id = (SELECT pid FROM sys_region AS c WHERE c.id = sys_region.pid) ) AS province" +
            " FROM sys_region WHERE id = #{id}")
    Map<String, Object> getPcdtByD(@Param("id") Long id);

    @Select("SELECT id AS adcode, deep, ext_name AS city," +
            " ( SELECT ext_name FROM sys_region AS p WHERE p.id = sys_region.pid ) AS province" +
            " FROM sys_region WHERE id = #{id}")
    Map<String, Object> getPcdtByC(@Param("id") Long id);

    @Select("SELECT id AS adcode, deep, ext_name AS province" +
            " FROM sys_region WHERE id = #{id}")
    Map<String, Object> getPcdtByP(@Param("id") Long id);

}
