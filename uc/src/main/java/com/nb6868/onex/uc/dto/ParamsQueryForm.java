package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BasePageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "参数查询")
public class ParamsQueryForm extends BasePageForm {

    @Query
    @ApiModelProperty(value = "参数编码")
    private String code;

    @Query
    @ApiModelProperty(value = "租户编码")
    private String tenantCode;

    @ApiModelProperty(value = "URL地址")
    @Query(column = "content->'$.url'")
    private String contentUrl;

}
