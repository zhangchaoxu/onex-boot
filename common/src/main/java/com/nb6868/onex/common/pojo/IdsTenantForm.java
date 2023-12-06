package com.nb6868.onex.common.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.jpa.Query;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @deprecated
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "ID数组租户请求")
public class IdsTenantForm extends IdsForm {

    @Query
    @Schema(description = "租户编码")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String tenantCode;

}
