package com.nb6868.onex.sys.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "区域查询")
public class RegionQueryForm extends BaseForm {

    @Query
    @ApiModelProperty("深度")
    private Integer deep;

    @Query(blurryType = Query.BlurryType.OR, column = "name,code,pcode")
    @ApiModelProperty("搜索关键词")
    private String search;

}
