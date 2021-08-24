package com.nb6868.onex.api.modules.uc.dao;

import com.nb6868.onex.api.modules.uc.entity.DeptEntity;
import com.nb6868.onex.common.dao.BaseDao;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 部门管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface DeptDao extends BaseDao<DeptEntity> {

    @Select("select uc_dept.*, " +
            "(select parent.name from uc_dept parent where parent.deleted = 0 and parent.id = uc_dept.pid) parentName from uc_dept " +
            "${ew.customSqlSegment}")
    @Override
    <E extends IPage<DeptEntity>> E selectPage(E page, Wrapper<DeptEntity> ew);

    @Select("select uc_dept.*, " +
            "(select parent.name from uc_dept parent where parent.deleted = 0 and parent.id = uc_dept.pid) parentName from uc_dept " +
            "${ew.customSqlSegment}")
    List<DeptEntity> selectList(Map<String, Object> params);

    @Override
    @Select("select t1.*, (select t2.name from uc_dept t2 where t2.deleted = 0 and t2.id = t1.pid) parentName" +
            " from uc_dept t1" +
            " where t1.deleted = 0 and t1.id = #{id}")
    DeptEntity selectById(@Param("id") Serializable id);

    /**
     * 获取所有部门的id、pid列表
     */
    @Select("select t1.id, t1.pid from uc_dept t1 where t1.deleted = 0")
    List<DeptEntity> getIdAndPidList();

    /**
     * 根据部门ID，获取所有子部门ID列表
     * @param id   部门ID
     */
    @Select("select id from uc_dept where deleted = 0 and pids like #{id}")
    List<Long> getSubDeptIdList(@Param("id") String id);

}
