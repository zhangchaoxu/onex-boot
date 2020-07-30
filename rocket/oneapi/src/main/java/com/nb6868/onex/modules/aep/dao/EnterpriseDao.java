package com.nb6868.onex.modules.aep.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nb6868.onex.booster.dao.BaseDao;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.modules.aep.entity.EnterpriseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * AEP-企业
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface EnterpriseDao extends BaseDao<EnterpriseEntity> {

    @Select("SELECT aep_enterprise.*, (SELECT count(id) FROM aep_device WHERE aep_device.deleted = 0 and aep_device.enterprise_id = aep_enterprise.id) AS device_count " +
            "FROM aep_enterprise " +
            "${ew.customSqlSegment}")
    @Override
    <E extends IPage<EnterpriseEntity>> E selectPage(@Param(Const.PAGE) E page, Wrapper<EnterpriseEntity> ew);

}
