package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "通过URL地址查询租户参数")
public class TenantParamsInfoByUrlForm extends BaseForm {

     @Schema(description = "地址")
    @NotEmpty(message="地址不能为空")
    private String url;

}
