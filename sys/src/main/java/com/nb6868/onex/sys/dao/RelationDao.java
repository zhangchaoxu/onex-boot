package com.nb6868.onex.sys.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.sys.entity.RelationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统-关系表
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface RelationDao extends BaseDao<RelationEntity> {

}
