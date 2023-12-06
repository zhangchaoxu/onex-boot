package com.nb6868.onex.uc.dto;

import cn.hutool.core.lang.tree.Tree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "权限范围")
public class MenuScopeResult implements Serializable {

    @Schema(description = "菜单树")
    private List<Tree<Long>> menuTree = new ArrayList<>();

    @Schema(description = "路由地址")
    private List<MenuResult> urlList = new ArrayList<>();

    @Schema(description = "权限列表")
    private Set<String> permissions = new HashSet<>();

    @Schema(description = "角色列表")
    private Set<String> roles = new HashSet<>();

}
