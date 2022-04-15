package com.nb6868.onex.common.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.jpa.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "ID请求")
public class IdForm extends BaseForm {

    @Query
    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "{id.require}")
    private Long id;

    @Query(type = Query.Type.LIMIT)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer pageSize = 1;

}
