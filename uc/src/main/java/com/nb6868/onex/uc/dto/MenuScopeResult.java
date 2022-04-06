package com.nb6868.onex.uc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "权限范围")
public class MenuScopeResult implements Serializable {

    @ApiModelProperty(value = "菜单树")
    private List<MenuTreeDTO> menuTree = new ArrayList<>();

    @ApiModelProperty(value = "路由地址")
    private List<MenuDTO> urlList = new ArrayList<>();

    @ApiModelProperty(value = "权限列表")
    private Set<String> permissions = new HashSet<>();

    @ApiModelProperty(value = "角色列表")
    private Set<String> roles = new HashSet<>();

}