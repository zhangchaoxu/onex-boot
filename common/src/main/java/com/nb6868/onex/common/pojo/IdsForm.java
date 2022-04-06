package com.nb6868.onex.common.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ID数组请求")
public class IdsForm extends BaseForm {

    @ApiModelProperty(value = "ids", required = true)
    @NotEmpty(message = "{ids.require}")
    private List<Long> ids;

}