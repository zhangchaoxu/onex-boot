package com.nb6868.onex.api.modules.msg.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 通知模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class NoticeTplExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "编号")
    private String code;
    @Excel(name = "模板名称")
    private String name;
    @Excel(name = "配置")
    private String config;
    @Excel(name = "参数")
    private String params;
    @Excel(name = "短信内容")
    private String content;
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
