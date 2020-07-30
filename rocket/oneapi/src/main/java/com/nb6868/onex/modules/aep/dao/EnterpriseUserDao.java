package com.nb6868.onex.modules.aep.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.nb6868.onex.booster.dao.BaseDao;
import com.nb6868.onex.modules.aep.entity.EnterpriseUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AEP-企业用户关联关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface EnterpriseUserDao extends BaseDao<EnterpriseUserEntity> {

    @Select("SELECT aep_enterprise_user.*, (SELECT openid FROM uc_user_oauth where uc_user_oauth.user_id = aep_enterprise_user.user_id) AS openid " +
            "FROM aep_enterprise_user " +
            "${ew.customSqlSegment}")
    @Override
    List<EnterpriseUserEntity> selectList(@Param(Constants.WRAPPER) Wrapper<EnterpriseUserEntity> queryWrapper);


}
