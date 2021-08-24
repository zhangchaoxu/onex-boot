package com.nb6868.onex.api.modules.sys.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 短地址
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ShorturlExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "标题")
    private String name;
    @Excel(name = "完整场地址")
    private String url;
    @Excel(name = "短地址路径")
    private String code;
    @Excel(name = "备注")
    private String remark;
    @Excel(name = "状态 0 不开放 1 开放")
    private Integer state;
    @Excel(name = "创建者")
    private Long createId;
    @Excel(name = "创建时间")
    private Date createTime;
    @Excel(name = "更新者")
    private Long updateId;
    @Excel(name = "更新时间")
    private Date updateTime;
    @Excel(name = "删除标记")
    private Integer deleted;

}
