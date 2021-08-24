package com.nb6868.onex.api.modules.pay.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 支付渠道
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ChannelExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "名称")
    private String name;
    @Excel(name = "排序")
    private Integer sort;
    @Excel(name = "支付类型 wechat alipay")
    private String type;
    @Excel(name = "参数")
    private String param;
    @Excel(name = "备注")
    private String remark;
    @Excel(name = "状态 0未启用 1启用")
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
    @Excel(name = "租户id")
    private Long tenantId;
    @Excel(name = "租户名称")
    private String tenantName;

}
