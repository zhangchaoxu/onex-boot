package com.nb6868.onex.common.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.jpa.Query;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "基础租户分页查询")
public class BaseTenantPageForm extends PageForm {

    @Query
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String tenantCode;

}
