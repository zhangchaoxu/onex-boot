package com.nb6868.onex.shop.modules.shop.service;

import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.shop.modules.shop.dao.CartDao;
import com.nb6868.onex.shop.modules.shop.dao.ReceiverDao;
import com.nb6868.onex.shop.modules.shop.entity.CartEntity;
import com.nb6868.onex.shop.modules.shop.entity.ReceiverEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 收货地址
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ReceiverService extends EntityService<ReceiverDao, ReceiverEntity> {

    /**
     * 通过id获得数据
     */
    public ReceiverEntity getByIdAndUserId(Long id, Long userId) {
        return query()
                .eq("user_id", userId)
                .eq("id", id)
                .last(Const.LIMIT_ONE)
                .one();
    }

}
