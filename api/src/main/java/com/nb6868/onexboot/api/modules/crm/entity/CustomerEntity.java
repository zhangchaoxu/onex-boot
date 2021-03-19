package com.nb6868.onexboot.api.modules.crm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onexboot.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CRM客户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("crm_customer")
public class CustomerEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 级别 1 重点 2 普通 3 非优先
     */
    private Integer level;
    /**
     * 客户来源
     */
    private String source;
    /**
     * 成交状态 0未成交 1已成交
     */
    private Integer dealState;
    /**
     * 联系人
     */
    private String contacts;
    /**
     * 联系电话
     */
    private String telephone;
    /**
     * 联系手机
     */
    private String mobile;
    /**
     * 内容
     */
    private String content;
    /**
     * 状态0 未激活 1 激活
     */
    private Integer state;
    /**
     * 区域名称,如浙江省,宁波市,鄞州区
     */
    private String regionName;
    /**
     * 区域编号,如33000,33010,33011
     */
    private String regionCode;
    /**
     * 详细门牌号
     */
    private String address;
    /**
     * 纬度
     */
    private Double lat;
    /**
     * 经度
     */
    private Double lng;
    /**
     * 标签
     */
    private String tags;
    /**
     * 备注
     */
    private String remark;
    /**
     * 租户id
     */
    @TableField(fill = FieldFill.INSERT)
    private Long tenantId;
    /**
     * 租户名称
     */
    @TableField(fill = FieldFill.INSERT)
    private String tenantName;

}
