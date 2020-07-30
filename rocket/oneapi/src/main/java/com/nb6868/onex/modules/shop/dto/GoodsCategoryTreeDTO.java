package com.nb6868.onex.modules.shop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.booster.util.TreeNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商品类别树结构
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "商品类别树结构")
public class GoodsCategoryTreeDTO extends TreeNode<GoodsCategoryTreeDTO> {

    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "店铺编码")
	private String storeCode;

	@ApiModelProperty(value = "名称")
	private String name;

	@ApiModelProperty(value = "logo")
	private String logo;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "介绍")
	private String content;

	@ApiModelProperty(value = "上级菜单名称")
	private String parentName;

	@ApiModelProperty(value = "上级菜单列表")
	private List<GoodsCategoryTreeDTO> parentMenuList;

	@ApiModelProperty(value = "租户id")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long tenantId;

	@ApiModelProperty(value = "租户名称")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String tenantName;
}
