package com.nb6868.onex.modules.aep.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * AEP-企业
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EnterpriseExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "名称")
    private String name;
    @Excel(name = "排序")
    private Integer sort;
    @Excel(name = "联系人")
    private String contacts;
    @Excel(name = "联系电话")
    private String telephone;
    @Excel(name = "备注")
    private String remark;
    @Excel(name = "内容")
    private String content;
    @Excel(name = "状态0 停用 1 正常")
    private Integer status;
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
    @Excel(name = "区域名称,如浙江省,宁波市,鄞州区")
    private String regionName;
    @Excel(name = "区域编号,如33000,33010,33011")
    private String regionCode;
    @Excel(name = "详细门牌号")
    private String address;
    @Excel(name = "纬度")
    private Double lat;
    @Excel(name = "经度")
    private Double lng;
    @Excel(name = "标签")
    private String tags;
    @Excel(name = "租户id")
    private Long tenantId;
    @Excel(name = "租户名称")
    private String tenantName;
    @Excel(name = "是否推送报警")
    private Integer alarmPush;
    @Excel(name = "漏电处理规则")
    private String interruptRule;

}
