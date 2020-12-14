package com.nb6868.onexboot.api.modules.msg.dao;

import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.msg.entity.MailTplEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface MailTplDao extends BaseDao<MailTplEntity> {

}
