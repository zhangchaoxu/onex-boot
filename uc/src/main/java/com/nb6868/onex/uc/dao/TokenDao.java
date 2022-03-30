package com.nb6868.onex.uc.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.uc.entity.TokenEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户Token
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface TokenDao extends BaseDao<TokenEntity> {

}
