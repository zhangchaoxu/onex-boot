package com.nb6868.onexboot.api.modules.msg.entity;

import com.nb6868.onexboot.common.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息推送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("msg_push_log")
public class PushLogEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 推送类型，10全部 20按alias 30按tag 40按alias和tag的合集
     */
    private Integer type;
    /**
     * 推送人alias
     */
    private String alias;
    /**
     * 推送人tag
     */
    private String tags;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 参数
     */
    private String params;
    /**
     * 发送结果
     */
    private String result;
    /**
     * 发送状态  0：失败  1：成功
     */
    private Integer status;

}
