package com.nb6868.onex.uc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.pojo.BasePageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "部门查询")
public class DeptQueryForm extends BaseForm {

    @Query
    @ApiModelProperty("类型")
    private Integer type;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "name,code,pcode")
    @ApiModelProperty("搜索关键词")
    private String search;

    @Query
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String tenantCode;
}
