package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "权限范围请求")
public class MenuScopeForm extends BaseForm {

    @ApiModelProperty(value = "是否包含角色信息")
    private boolean roles = false;

    @ApiModelProperty(value = "是否包含权限信息")
    private boolean permissions = true;

}
