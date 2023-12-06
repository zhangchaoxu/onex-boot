package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.EnumValue;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "菜单权限")
public class MenuDTO extends BaseDTO {

	 @Schema(description = "上级ID，一级菜单为0")
	@NotNull(message="请选择上级", groups = DefaultGroup.class)
	private Long pid;

	 @Schema(description = "类型 0菜单/页面,1按钮/接口")
	@EnumValue(intValues = {0, 1},message = "{type.range}", groups = DefaultGroup.class)
	private Integer type;

	 @Schema(description = "名称")
	@NotBlank(message = "{name.require}", groups = DefaultGroup.class)
	private String name;

	 @Schema(description = "是否显示")
	private Integer showMenu;

	 @Schema(description = "菜单或页面URL")
	private String url;

	 @Schema(description = "菜单新页面打开")
	private Integer urlNewBlank;

	 @Schema(description = "授权(多个用逗号分隔，如：sys:user:list,sys:user:save)")
	private String permissions;

	 @Schema(description = "菜单图标")
	private String icon;

	 @Schema(description = "排序")
	@Min(value = 0, message = "{sort.number}", groups = DefaultGroup.class)
	private Integer sort;

	 @Schema(description = "租户编码")
	private String tenantCode;

	 @Schema(description = "上级菜单列表")
	private List<MenuDTO> parentMenuList;
}
