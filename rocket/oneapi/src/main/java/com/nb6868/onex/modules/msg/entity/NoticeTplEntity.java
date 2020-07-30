package com.nb6868.onex.modules.msg.entity;

import com.nb6868.onex.booster.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("msg_notice_tpl")
public class NoticeTplEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private String code;
    /**
     * 模板名称
     */
    private String name;
    /**
     * 配置
     */
    private String config;
    /**
     * 参数
     */
    private String params;
    /**
     * 短信内容
     */
    private String content;
}
