package com.nb6868.onex.uc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "菜单")
public class MenuResult implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "上级ID，一级菜单为0")
    private Long pid;

    @ApiModelProperty(value = "类型 0菜单/页面,1按钮/接口")
    private Integer type;

    @ApiModelProperty(value = "名称")
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
    private Integer sort;

}
