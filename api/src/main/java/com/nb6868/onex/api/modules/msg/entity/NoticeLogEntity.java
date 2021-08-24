package com.nb6868.onex.api.modules.msg.entity;

import com.nb6868.onex.common.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("msg_notice_log")
public class NoticeLogEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    private Long tplId;
    /**
     * 模板编码
     */
    private String tplCode;
    /**
     * 收件人id
     */
    private Long userId;
    /**
     * 表主键
     */
    private Long tableId;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 标题
     */
    private String subject;
    /**
     * 正文
     */
    private String content;
    /**
     * 发送状态  0:失败  1: 成功
     */
    private Integer state;
    /**
     * 是否已读  0:未读 1: 已读
     */
    private Integer readed;

}
