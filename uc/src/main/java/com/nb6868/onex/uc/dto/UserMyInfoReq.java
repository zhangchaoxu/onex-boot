package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BaseReq;
import com.nb6868.onex.common.pojo.PageReq;
import com.nb6868.onex.common.validator.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "我的用户信息查询")
public class UserMyInfoReq extends BaseReq {

    @Schema(description = "需要角色信息, 0/1")
    @EnumValue(message = "角色信息参数为0和1", enumClass = Const.BooleanEnum.class)
    private Integer roleNeeded = 0;

    @Schema(description = "需要部门信息, 0/1")
    @EnumValue(message = "部门信息参数为0和1", enumClass = Const.BooleanEnum.class)
    private Integer deptNeeded = 0;

}
