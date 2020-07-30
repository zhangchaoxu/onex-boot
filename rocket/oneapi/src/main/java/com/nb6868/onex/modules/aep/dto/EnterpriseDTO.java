package com.nb6868.onex.modules.aep.dto;

import com.nb6868.onex.booster.pojo.BaseTenantDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AEP-企业
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "AEP-企业")
public class EnterpriseDTO extends BaseTenantDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "名称")
	private String name;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "联系人")
	private String contacts;

	@ApiModelProperty(value = "联系电话")
	private String telephone;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "内容")
	private String content;

	@ApiModelProperty(value = "状态0 停用 1 正常")
	private Integer status;

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

	@ApiModelProperty(value = "标签")
	private String tags;

	@ApiModelProperty(value = "是否推送报警")
	private Integer alarmPush;

	@ApiModelProperty(value = "漏电处理规则")
	private String interruptRule;

	@ApiModelProperty(value = "设备数量")
	private Long deviceCount;

}
