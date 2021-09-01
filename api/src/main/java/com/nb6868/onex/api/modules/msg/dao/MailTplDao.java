package com.nb6868.onex.api.modules.msg.dao;

import com.nb6868.onex.api.modules.msg.entity.MailTplEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface MailTplDao extends BaseDao<MailTplEntity> {

}
