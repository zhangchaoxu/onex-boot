package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseIdReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "更新用户菜单关系请求")
public class UserUpdateMenuScopeReq extends BaseIdReq {

    @Schema(description = "菜单ID列表")
    @NotNull(message = "菜单id不能为null,若无部门请传空数组")
    private List<Long> menuIds;

}
