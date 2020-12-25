package com.nb6868.onexboot.api.modules.uc.dao;

import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.uc.entity.TokenEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户Token
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface TokenDao extends BaseDao<TokenEntity> {

}
