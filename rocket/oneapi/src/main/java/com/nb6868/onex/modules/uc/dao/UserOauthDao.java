package com.nb6868.onex.modules.uc.dao;

import com.nb6868.onex.booster.dao.BaseDao;
import com.nb6868.onex.modules.uc.entity.UserOauthEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 第三方用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface UserOauthDao extends BaseDao<UserOauthEntity> {

}
