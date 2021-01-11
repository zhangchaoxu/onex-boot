package com.nb6868.onexboot.api.modules.msg.dao;

import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.msg.entity.NoticeLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface NoticeLogDao extends BaseDao<NoticeLogEntity> {

}
