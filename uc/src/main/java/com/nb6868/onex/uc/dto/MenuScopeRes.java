package com.nb6868.onex.uc.dto;

import cn.hutool.core.lang.tree.Tree;
import com.nb6868.onex.common.pojo.BaseRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "权限范围结果")
public class MenuScopeRes extends BaseRes {

    @Schema(description = "菜单树")
    private List<Tree<Long>> menuTree = new ArrayList<>();

    @Schema(description = "路由地址")
    private List<MenuRes> urlList = new ArrayList<>();

    @Schema(description = "权限列表")
    private List<String> permissions = new ArrayList<>();

    @Schema(description = "角色列表")
    private List<Long> roleIds = new ArrayList<>();

    @Schema(description = "角色编码列表")
    private List<String> roleCodes = new ArrayList<>();

}
