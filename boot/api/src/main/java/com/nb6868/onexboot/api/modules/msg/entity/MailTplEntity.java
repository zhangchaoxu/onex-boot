package com.nb6868.onexboot.api.modules.msg.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onexboot.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 消息模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("msg_mail_tpl")
@Alias("msg_mail_tpl")
public class MailTplEntity extends BaseEntity {

    /**
     * 名称
     */
	private String name;
    /**
     * 编码
     */
	private String code;
    /**
     * 类型 短信sms 电邮email 微信模板消息wx_template 站内信 notice
     */
	private String type;
    /**
     * 标题
     */
	private String title;
    /**
     * 内容
     */
	private String content;
    /**
     * 配置参数
     */
	private String param;
	/**
	 * 限时秒 -1表示不限
	 */
	private Integer timeLimit;
}
