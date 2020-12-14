package com.nb6868.onexboot.api.modules.pay.dto;

import com.nb6868.onexboot.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 支付渠道
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "支付渠道")
public class ChannelDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "名称")
	private String name;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "支付类型")
	private String payType;

	@ApiModelProperty(value = "参数")
	private String param;

	@ApiModelProperty(value = "回调地址")
	private String notifyUrl;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "状态 0未启用 1启用")
	private Integer status;

	@ApiModelProperty(value = "租户id")
	private Long tenantId;

	@ApiModelProperty(value = "租户名称")
	private String tenantName;

}
