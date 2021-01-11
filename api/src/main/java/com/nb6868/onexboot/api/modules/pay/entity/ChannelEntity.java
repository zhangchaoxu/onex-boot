package com.nb6868.onexboot.api.modules.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onexboot.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 支付渠道
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pay_channel")
public class ChannelEntity extends BaseEntity {
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
     * 支付类型
     */
    private String payType;
    /**
     * 回调地址
     */
    private String notifyUrl;
    /**
     * 参数
     */
    private String param;
    /**
     * 备注
     */
    private String remark;
    /**
     * 状态 0未启用 1启用
     */
    private Integer status;
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 租户名称
     */
    private String tenantName;
}
