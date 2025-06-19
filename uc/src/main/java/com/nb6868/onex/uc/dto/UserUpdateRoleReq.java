package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.IdReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "更新用户角色关系请求")
public class UserUpdateRoleReq extends IdReq {

    @Schema(description = "角色ID列表")
    @NotNull(message = "角色id不能为null,若无角色请传空数组")
    private List<Long> roleIds;

    @Schema(description = "用户角色关系,预留,传0或者null")
    private Integer type;

}
