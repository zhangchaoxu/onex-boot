package com.nb6868.onex.shop.modules.uc.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.shop.modules.uc.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface UserDao extends BaseDao<UserEntity> {

}
