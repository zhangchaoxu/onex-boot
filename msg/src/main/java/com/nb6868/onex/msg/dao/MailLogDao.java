package com.nb6868.onex.msg.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.msg.entity.MailLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface MailLogDao extends BaseDao<MailLogEntity> {

}
