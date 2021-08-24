package com.nb6868.onex.api.modules.uc.dao;

import com.nb6868.onex.api.modules.uc.entity.TokenEntity;
import com.nb6868.onex.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户Token
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface TokenDao extends BaseDao<TokenEntity> {

}
