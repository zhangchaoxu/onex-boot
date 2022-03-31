package com.nb6868.onex.common.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ID请求")
public class IdForm extends BaseForm {

    @ApiModelProperty(value = "id")
    @NotNull(message = "{id.require}")
    private Long id;

}
