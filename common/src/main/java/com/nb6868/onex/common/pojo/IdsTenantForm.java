package com.nb6868.onex.common.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.jpa.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ID数组租户请求")
public class IdsTenantForm extends IdsForm {

    @Query
    @ApiModelProperty("租户编码")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String tenantCode;

}
