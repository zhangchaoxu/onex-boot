package com.nb6868.onexboot.api.modules.msg.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onexboot.common.pojo.BaseIdStringEntity;
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
public class MailTplEntity extends BaseIdStringEntity {

    /**
     * 名称
     */
	private String name;
    /**
     * 渠道 短信sms 电邮email 微信模板消息wx_template 站内信notice
     */
	private String channel;
	/**
	 * 类型 1验证码 2状态通知 3营销消息
	 */
	private Integer type;
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