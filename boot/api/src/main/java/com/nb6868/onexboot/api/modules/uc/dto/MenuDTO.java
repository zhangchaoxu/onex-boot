package com.nb6868.onexboot.api.modules.uc.dto;

import com.nb6868.onexboot.common.pojo.BaseDTO;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 菜单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "菜单")
public class MenuDTO extends BaseDTO {
    /**
     * 上级ID
     */
    @ApiModelProperty(value = "上级ID")
    @NotNull(message="请选择上级", groups = DefaultGroup.class)
    private Long pid;

    @ApiModelProperty(value = "菜单名称")
    @NotBlank(message = "{name.require}", groups = DefaultGroup.class)
    private String name;

    @ApiModelProperty(value = "菜单英文名称")
    private String nameEn;

    @ApiModelProperty(value = "菜单URL")
    private String url;

    @ApiModelProperty(value = "是否显示")
    private Integer showMenu;

    @ApiModelProperty(value = "菜单新页面打开")
    private Integer urlNewBlank;

    @ApiModelProperty(value = "类型  0:菜单/页面   1:按钮/接口")
    @Range(min = 0, max = 2, message = "{type.range}", groups = DefaultGroup.class)
    private Integer type;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty(value = "权限")
    private String permissions;

    @ApiModelProperty(value = "排序")
    @Min(value = 0, message = "{sort.number}", groups = DefaultGroup.class)
    private Integer sort;

    @ApiModelProperty(value = "上级菜单列表")
    private List<MenuDTO> parentMenuList;

}
