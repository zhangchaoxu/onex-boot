package com.nb6868.onex.api.modules.uc.dao;

import com.nb6868.onex.api.modules.uc.entity.UserOauthEntity;
import com.nb6868.onex.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 第三方用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface UserOauthDao extends BaseDao<UserOauthEntity> {

}
