package com.nb6868.onex.api.modules.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

/**
 * CRM客户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "CRM客户")
public class CustomerDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "名称", required = true)
	@NotBlank(message = "{name.require}", groups = DefaultGroup.class)
	private String name;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "级别 1 重点 2 普通 3 非优先")
	@Range(min = 1, max = 3, message = "级别取值1-3", groups = DefaultGroup.class)
	private Integer level;

	@ApiModelProperty(value = "客户来源")
	private String source;

	@ApiModelProperty(value = "标签")
	private String tags;

	@ApiModelProperty(value = "成交状态 0未成交 1已成交")
	@Range(min = 0, max = 1, message = "成交状态取值01", groups = DefaultGroup.class)
	private Integer dealState;

	@ApiModelProperty(value = "联系人")
	private String contacts;

	@ApiModelProperty(value = "联系电话")
	private String telephone;

	@ApiModelProperty(value = "联系手机")
	private String mobile;

	@ApiModelProperty(value = "内容")
	private String content;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "状态0 未激活 1 激活")
	private Integer state;

	@ApiModelProperty(value = "区域名称,如浙江省,宁波市,鄞州区")
	private String regionName;

	@ApiModelProperty(value = "区域编号,如33000,33010,33011")
	private String regionCode;

	@ApiModelProperty(value = "详细门牌号")
	private String address;

	@ApiModelProperty(value = "纬度")
	private Double lat;

	@ApiModelProperty(value = "经度")
	private Double lng;

	@ApiModelProperty(value = "租户id")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long tenantId;

	@ApiModelProperty(value = "租户名称")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String tenantName;

}
