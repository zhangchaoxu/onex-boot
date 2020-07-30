package com.nb6868.onex.modules.msg.dao;

import com.nb6868.onex.booster.dao.BaseDao;
import com.nb6868.onex.modules.msg.entity.NoticeLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface NoticeLogDao extends BaseDao<NoticeLogEntity> {

}
