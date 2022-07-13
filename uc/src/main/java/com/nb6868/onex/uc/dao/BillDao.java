package com.nb6868.onex.uc.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.uc.entity.BillEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户中心-账单流水
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface BillDao extends BaseDao<BillEntity> {

}
