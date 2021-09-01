package com.nb6868.onex.api.modules.msg.dao;

import com.nb6868.onex.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface MailLogDao extends BaseDao<MailLogEntity> {

}
