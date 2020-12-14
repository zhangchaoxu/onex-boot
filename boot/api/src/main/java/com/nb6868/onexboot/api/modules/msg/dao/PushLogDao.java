package com.nb6868.onexboot.api.modules.msg.dao;

import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.msg.entity.PushLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息推送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface PushLogDao extends BaseDao<PushLogEntity> {

}
