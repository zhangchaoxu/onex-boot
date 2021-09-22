package com.nb6868.onex.shop.modules.shop.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.shop.modules.shop.entity.CartEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface CartDao extends BaseDao<CartEntity> {

}
