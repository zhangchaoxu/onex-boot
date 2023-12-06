package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.EnumValue;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * 菜单权限
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "菜单权限")
public class MenuDTO extends BaseDTO {

	@ApiModelProperty(value = "上级ID，一级菜单为0")
	@NotNull(message="请选择上级", groups = DefaultGroup.class)
	private Long pid;

	@ApiModelProperty(value = "类型 0菜单/页面,1按钮/接口")
	@EnumValue(intValues = {0, 1},message = "{type.range}", groups = DefaultGroup.class)
	private Integer type;

	@ApiModelProperty(value = "名称")
	@NotBlank(message = "{name.require}", groups = DefaultGroup.class)
	private String name;

	@ApiModelProperty(value = "是否显示")
	private Integer showMenu;

	@ApiModelProperty(value = "菜单或页面URL")
	private String url;

	@ApiModelProperty(value = "菜单新页面打开")
	private Integer urlNewBlank;

	@ApiModelProperty(value = "授权(多个用逗号分隔，如：sys:user:list,sys:user:save)")
	private String permissions;

	@ApiModelProperty(value = "菜单图标")
	private String icon;

	@ApiModelProperty(value = "排序")
	@Min(value = 0, message = "{sort.number}", groups = DefaultGroup.class)
	private Integer sort;

	@ApiModelProperty(value = "租户编码")
	private String tenantCode;

	@ApiModelProperty(value = "上级菜单列表")
	private List<MenuDTO> parentMenuList;
}
