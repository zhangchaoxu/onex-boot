package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseIdReq;
import com.nb6868.onex.common.pojo.IdReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "更新用户部门关系请求")
public class UserUpdateDeptReq extends IdReq {

    @Schema(description = "部门ID列表")
    @NotNull(message = "部门id不能为null,若无部门请传空数组")
    private List<Long> deptIds;

    @Schema(description = "用户角色关系,预留,传0或者null")
    private Integer type;

}
