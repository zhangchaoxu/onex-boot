package com.nb6868.onexboot.api.modules.msg.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 通知发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class NoticeLogExcel {
    @Excel(name = "模板编码")
    private String tplCode;
    @Excel(name = "收件人id")
    private Long userId;
    @Excel(name = "表主键")
    private Long tableId;
    @Excel(name = "表名")
    private String tableName;
    @Excel(name = "标题")
    private String subject;
    @Excel(name = "正文")
    private String content;
    @Excel(name = "发送状态  0:失败  1: 成功")
    private Integer status;
    @Excel(name = "是否已读  0:未读 1: 已读")
    private Integer read;
    @Excel(name = "创建者")
    private Long creator;
    @Excel(name = "创建时间")
    private Date createDate;
    @Excel(name = "更新者")
    private Long updater;
    @Excel(name = "更新时间")
    private Date updateDate;
    @Excel(name = "软删标记")
    private Integer delFlag;

}
