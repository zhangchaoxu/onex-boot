package com.nb6868.onex.common.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.jpa.Query;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "基础租户查询")
public class BaseTenantForm extends BaseForm {

    @Query
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String tenantCode;

}
