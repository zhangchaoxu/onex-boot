package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "通过URL地址查询租户参数")
public class TenantParamsInfoByUrlForm extends BaseForm {

    @ApiModelProperty(value = "地址")
    @NotEmpty(message="地址不能为空")
    private String url;

}
