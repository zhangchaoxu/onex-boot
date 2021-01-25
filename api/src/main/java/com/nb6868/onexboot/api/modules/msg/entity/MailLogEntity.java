package com.nb6868.onexboot.api.modules.msg.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onexboot.common.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * 消息记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("msg_mail_log")
@Alias("msg_mail_log")
public class MailLogEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 模板编码
     */
	private String tplCode;
    /**
     * 消息类型
     */
	private Integer tplType;
    /**
     * 发送者
     */
	private String mailFrom;
	/**
	 * 抄送
	 */
	private String mailCc;
    /**
     * 收件人
     */
	private String mailTo;
    /**
     * 标题
     */
	private String subject;
    /**
     * 内容参数
     */
	private String contentParams;
    /**
     * 内容
     */
	private String content;
    /**
     * 消费状态 0 :  未消费 1 ：已消费
     */
	private Integer consumeStatus;
    /**
     * 发送状态  0：失败  1：成功
     */
	private Integer status;
    /**
     * 发送结果
     */
	private String result;

	/**
	 * 有效期结束时间
	 */
	private Date validEndTime;
}
