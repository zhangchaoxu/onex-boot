package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "通过编码查询租户参数")
public class TenantParamsInfoByCodeForm extends BaseForm {

    @ApiModelProperty(value = "编码")
    @NotEmpty(message="编码不能为空")
    private String code;

}
