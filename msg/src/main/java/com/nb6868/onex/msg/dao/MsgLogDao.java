package com.nb6868.onex.msg.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.msg.entity.MsgLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface MsgLogDao extends BaseDao<MsgLogEntity> {

}
