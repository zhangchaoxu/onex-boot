package com.nb6868.onex.modules.uc.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TenantExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "名称")
    private String name;
    @Excel(name = "有效期开始")
    private Date validStartTime;
    @Excel(name = "有效期结束")
    private Date validEndTime;
    @Excel(name = "状态 0 无效 1 有效")
    private Integer status;
    @Excel(name = "创建者id")
    private Long createId;
    @Excel(name = "创建时间")
    private Date createTime;
    @Excel(name = "更新者id")
    private Long updateId;
    @Excel(name = "更新时间")
    private Date updateTime;
    @Excel(name = "逻辑删除")
    private Integer deleted;

}
