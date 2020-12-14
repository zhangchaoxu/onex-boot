package com.nb6868.onexboot.api.modules.log.dao;

import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.log.entity.LoginEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Mapper
public interface LoginDao extends BaseDao<LoginEntity> {

}
