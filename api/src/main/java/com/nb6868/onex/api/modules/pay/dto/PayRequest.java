package com.nb6868.onex.api.modules.pay.dto;

import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 支付请求
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "支付请求")
public class PayRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单号不能为空", required = true)
    @NotNull(message = "订单号不能为空", groups = DefaultGroup.class)
    private Long orderId;

    @ApiModelProperty(value = "支付类型", required = true)
    @NotBlank(message = "支付类型不能为空", groups = DefaultGroup.class)
    private String payType;

    @ApiModelProperty(value = "用户openid,部分情况需要")
    private String openid;

}
