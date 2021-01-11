package com.nb6868.onexboot.api.modules.sys.dao;

import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.sys.entity.RegionEntity;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface RegionDao extends BaseDao<RegionEntity> {

    String PLIST_SQL = " FROM " +
            " (SELECT @r AS _id, (SELECT @r := pid FROM sys_region WHERE id = _id ) AS pid,  @l := @l + 1 AS lvl FROM ( SELECT @r := #{id}, @l := 0 ) vars, b_node h WHERE @r != 0) t1" +
            " JOIN b_node t2 ON t1._id = t2.id" +
            " WHERE t2.del_flag = 0 ORDER BY t1.lvl DESC";

    /**
     * 获取数据与数据子节点信息
     * @param ew
     * @return
     */
    @Select("SELECT (select COUNT(*) from sys_region as child where child.pid = sys_region.id) as childNum, sys_region.*" +
            " FROM sys_region ${ew.customSqlSegment}")
    List<RegionEntity> selectListWithChildNum(@Param(Constants.WRAPPER) Wrapper<RegionEntity> ew);

}
