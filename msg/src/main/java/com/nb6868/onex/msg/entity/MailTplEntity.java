package com.nb6868.onex.msg.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
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
	 * 编码
	 */
	private String code;
    /**
     * 名称
     */
	private String name;
    /**
     * 渠道 短信sms 电邮email 微信模板消息wx_template 站内信notice
     */
	private String channel;
	/**
	 * 平台
	 */
	private String platform;
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
	/**
	 * 收件人黑名单
	 */
	private String mailToBlack;
	/**
	 * 收件人魔术放行
	 */
	private String mailToMagic;
	/**
	 * 验证码生成器
	 */
	private String codeGenerator;

}
