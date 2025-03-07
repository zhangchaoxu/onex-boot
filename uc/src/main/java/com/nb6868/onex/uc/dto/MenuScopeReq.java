package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "权限范围请求")
public class MenuScopeReq extends BaseReq {

    @Schema(description = "是否包含角色编码信息", defaultValue = "false")
    private boolean roleCodes = false;

    @Schema(description = "是否包含角色ID信息", defaultValue = "false")
    private boolean roleIds = false;

    @Schema(description = "是否包含权限信息", defaultValue = "true")
    private boolean permissions = true;

}
