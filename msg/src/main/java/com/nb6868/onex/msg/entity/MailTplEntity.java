package com.nb6868.onex.msg.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 消息-模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "msg_mail_tpl", autoResultMap = true)
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
     * 类型 1验证码 2状态通知 3营销消息
     */
    private Integer type;
    /**
     * 渠道,短信sms/电邮email/微信公众号模板消息wx_mp_template/微信小程序订阅消息wx_ma_subscribe/站内信 notice
     */
    private String channel;
    /**
     * 平台 如aliyun/juhe
     */
    private String platform;
    /**
     * 限时秒数 -1表示不限
     */
    private Integer timeLimit;
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
    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject param;
    /**
     * 租户编码
     */
    private String tenantCode;
}
