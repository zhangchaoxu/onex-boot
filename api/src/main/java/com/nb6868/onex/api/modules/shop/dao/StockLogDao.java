package com.nb6868.onex.api.modules.shop.dao;

import com.nb6868.onex.api.modules.shop.entity.StockLogEntity;
import com.nb6868.onex.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出入库记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface StockLogDao extends BaseDao<StockLogEntity> {

}
