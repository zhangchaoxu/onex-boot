package com.nb6868.onex.shop.modules.log.service;

import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.shop.modules.log.dao.OperationDao;
import com.nb6868.onex.shop.modules.log.entity.OperationEntity;
import org.springframework.stereotype.Service;

/**
 * 操作日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OperationService extends EntityService<OperationDao, OperationEntity> {

}
