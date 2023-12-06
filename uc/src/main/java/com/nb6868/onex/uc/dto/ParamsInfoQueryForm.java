package com.nb6868.onex.uc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "参数查询")
public class ParamsInfoQueryForm extends BaseForm {

    @ApiModelProperty(value = "参数编码")
    @NotEmpty(message="编码不能为空")
    @Query
    private String code;

    @ApiModelProperty(value = "租户编码")
    @Query
    private String tenantCode;

    @ApiModelProperty(value = "URL地址")
    @Query(column = "content->'$.url'")
    private String contentUrl;

    @Query(type = Query.Type.LIMIT)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer pageSize = 1;

}
