package com.nb6868.onex.uc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "菜单")
public class MenuResult implements Serializable {

     @Schema(description = "id")
    private Long id;

     @Schema(description = "上级ID，一级菜单为0")
    private Long pid;

     @Schema(description = "类型 0菜单/页面,1按钮/接口")
    private Integer type;

     @Schema(description = "名称")
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
    private Integer sort;

}
