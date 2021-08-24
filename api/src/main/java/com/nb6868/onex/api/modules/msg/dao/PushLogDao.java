package com.nb6868.onex.api.modules.msg.dao;

import com.nb6868.onex.api.modules.msg.entity.PushLogEntity;
import com.nb6868.onex.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息推送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface PushLogDao extends BaseDao<PushLogEntity> {

}
