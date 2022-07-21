package com.nb6868.onex.uc.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.uc.entity.PostEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 岗位管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface PostDao extends BaseDao<PostEntity> {

}
