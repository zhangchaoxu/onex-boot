package com.nb6868.onex.msg.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.nb6868.onex.common.pojo.BaseEntity;
import com.nb6868.onex.common.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * 消息-记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "msg_mail_log", autoResultMap = true)
@Alias("msg_mail_log")
public class MailLogEntity extends BaseTenantEntity {

    /**
     * 模板编码
     */
    private String tplCode;
    /**
     * 验证码
     */
    private String code;
    /**
     * 有效期结束
     */
    private Date validEndTime;
    /**
     * 发送者
     */
    private String mailFrom;
    /**
     * 收件人
     */
    private String mailTo;
    /**
     * 抄送
     */
    private String mailCc;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容参数
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject contentParams;
    /**
     * 内容
     */
    private String content;
    /**
     * 消费状态
     */
    private Integer consumeState;
    /**
     * 发送状态
     */
    private Integer state;
    /**
     * 发送结果
     */
    private String result;
}
