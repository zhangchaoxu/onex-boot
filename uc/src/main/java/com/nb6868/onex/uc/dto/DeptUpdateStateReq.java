package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.validator.EnumValue;
import com.nb6868.onex.uc.UcConst;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "部门更新状态请求")
public class DeptUpdateStateReq extends IdReq {

    @Schema(description = "状态")
    @EnumValue(enumClass = UcConst.DeptStateEnum.class, message = "状态传参错误")
    private Integer state;

}
