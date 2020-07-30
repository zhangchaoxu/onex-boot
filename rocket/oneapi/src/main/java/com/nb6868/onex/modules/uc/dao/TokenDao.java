package com.nb6868.onex.modules.uc.dao;

import com.nb6868.onex.booster.dao.BaseDao;
import com.nb6868.onex.modules.uc.entity.TokenEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户Token
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Mapper
public interface TokenDao extends BaseDao<TokenEntity> {

}
