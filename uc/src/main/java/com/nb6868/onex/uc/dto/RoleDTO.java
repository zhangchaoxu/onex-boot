package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseStringDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "角色")
public class RoleDTO extends BaseStringDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "角色编码")
	@NotBlank(message = "请输入角色编码", groups = DefaultGroup.class)
	@Length(min = 1, max = 50, message = "请限制编码1-50字", groups = DefaultGroup.class)
	private String id;

	@ApiModelProperty(value = "名称")
	@NotBlank(message = "{name.require}", groups = DefaultGroup.class)
	private String name;

	@ApiModelProperty(value = "排序")
	@NotNull(message = "排序不能为空", groups = DefaultGroup.class)
	private Integer sort;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "租户编码")
	private String tenantCode;

	@ApiModelProperty(value = "菜单ID列表")
	private List<Long> menuIdList;

}
