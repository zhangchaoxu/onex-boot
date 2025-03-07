package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "我的用户信息查询")
public class UserMyInfoReq extends BaseReq {

    @Schema(description = "需要角色信息", defaultValue = "false")
    private boolean roleNeeded = false;

    @Schema(description = "需要部门信息", defaultValue = "false")
    private boolean deptNeeded = false;

}
