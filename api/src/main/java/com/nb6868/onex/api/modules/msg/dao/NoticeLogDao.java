package com.nb6868.onex.api.modules.msg.dao;

import com.nb6868.onex.api.modules.msg.entity.NoticeLogEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface NoticeLogDao extends BaseDao<NoticeLogEntity> {

}
