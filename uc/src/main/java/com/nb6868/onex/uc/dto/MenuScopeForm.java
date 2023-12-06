package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "权限范围请求")
public class MenuScopeForm extends BaseForm {

     @Schema(description = "是否包含角色信息", example = "false")
    private boolean roles = false;

     @Schema(description = "是否包含权限信息", example = "true")
    private boolean permissions = true;

}
