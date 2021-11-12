package com.nb6868.onex.msg.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.msg.entity.MailTplEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface MailTplDao extends BaseDao<MailTplEntity> {

}
