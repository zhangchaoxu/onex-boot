package com.nb6868.onex.api.modules.sys.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.api.modules.sys.entity.DictEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据字典
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface DictDao extends BaseDao<DictEntity> {

}
