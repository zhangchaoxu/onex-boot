package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.jpa.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ID数组请求")
public class StringIdsForm extends BaseForm {

    @Query(type = Query.Type.IN, column = "id")
    @ApiModelProperty(value = "ids", required = true)
    @NotEmpty(message = "{ids.require}")
    private List<String> ids;

}
