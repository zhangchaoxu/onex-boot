package com.nb6868.onexboot.api.modules.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 订单收货信息修改请求
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "订单收货信息修改请求")
public class OrderChangeReceiverRequest implements Serializable {

    @ApiModelProperty(value = "收货地址id")
    private Long receiverId;

    @ApiModelProperty(value = "收件人")
    private String receiverConsignee;

    @ApiModelProperty(value = "收件人电话")
    private String receiverMobile;

    @ApiModelProperty(value = "收件详细地址")
    private String receiverAddress;

    @ApiModelProperty(value = "收件地址区域编码")
    private String receiverRegionCode;

    @ApiModelProperty(value = "收件地址区域")
    private String receiverRegionName;

}
