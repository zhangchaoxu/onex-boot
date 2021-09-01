package com.nb6868.onex.api.modules.log.dao;

import com.nb6868.onex.api.modules.log.entity.LoginEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface LoginDao extends BaseDao<LoginEntity> {

}
